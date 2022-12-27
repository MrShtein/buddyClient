package mr.shtein.kennel.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.*
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.KennelPreview
import mr.shtein.data.repository.KennelRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.ui.adapter.KennelsAdapter
import mr.shtein.kennel.util.KennelDiffUtil
import mr.shtein.network.ImageLoader
import mr.shtein.ui_util.FragmentsListForAssigningAnimation
import mr.shtein.ui_util.setInsetsListenerForPadding
import mr.shtein.ui_util.setStatusBarColor
import org.koin.android.ext.android.inject
import java.net.ConnectException
import java.net.SocketTimeoutException

private const val LAST_FRAGMENT_KEY = "last_fragment"


class AddKennelFragment : Fragment(R.layout.add_kennel_fragment) {

    companion object {
        private const val KENNEL_ITEM_BUNDLE_KEY = "kennel_item_key"
    }

    private lateinit var kennelsBtn: MaterialButton
//    private lateinit var volunteersBtn: MaterialButton
    private lateinit var kennelsUnderscore: View
    private lateinit var volunteersUnderscore: View
    private lateinit var descriptionView: TextView
    private lateinit var addKennelBtn: MaterialButton
    private lateinit var kennelRecycler: RecyclerView
    private lateinit var kennelAdapter: KennelsAdapter
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var kennelsList = mutableListOf<KennelPreview>()
    private var isReadyKennelsList = false
    private var volunteersList = mutableListOf<KennelPreview>()
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val networkImageLoader: ImageLoader by inject()
    private val networkKennelRepository: KennelRepository by inject()
    private val navigator: KennelNavigation by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        navigator.addTransitionsForKennelFlow(this)
        val fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation? =
            arguments?.getParcelable(LAST_FRAGMENT_KEY)
        if (fragmentsListForAssigningAnimation != null) {
            changeAnimationsWhenStartFragment(fragmentsListForAssigningAnimation)
        }
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.let {
            setStatusBarColor(true)
            setInsetsListenerForPadding(it, left = false, top = true, right = false, bottom = false)
            initViews(it)
            setListeners()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coroutineScope.launch {
            val personId = userPropertiesRepository.getUserId()
            try {
                getKennels(personId)
                initRecyclerView(view)
                if (kennelsList.size == 0) {
                    showDescriptionText(R.string.add_kennel_fragment_empty_kennels_text)
                }
            } catch (error: ServerErrorException) {
                val errorText = requireContext().getString(R.string.server_unavailable_msg)
                showError(errorText)
            } catch (error: SocketTimeoutException) {
                val errorText = requireContext().getString(R.string.internet_failure_text)
                showError(errorText)
            } catch (error: ConnectException) {
                val errorText = requireContext().getString(R.string.internet_failure_text)
                showError(errorText)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        kennelsList = mutableListOf()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun initViews(view: View) {
        kennelsBtn = view.findViewById(R.id.add_kennel_fragment_add_btn)
//        volunteersBtn = view.findViewById(R.id.add_kennel_fragment_volunteers_btn)
//        kennelsUnderscore = view.findViewById(R.id.add_kennel_fragment_kennel_underscore)
//        volunteersUnderscore = view.findViewById(R.id.add_kennel_fragment_volunteer_underscore)
        descriptionView = view.findViewById(R.id.add_kennel_fragment_kennels_or_volunteers_absence)
        addKennelBtn = view.findViewById(R.id.add_kennel_fragment_add_btn)
    }

    private fun setListeners() {
        kennelsBtn.setOnClickListener {
            val volunteersUnderscoreVisibility = volunteersUnderscore.isVisible
            addKennelBtn.visibility = View.VISIBLE
            if (volunteersUnderscoreVisibility) {
                volunteersUnderscore.isVisible = false
                kennelsUnderscore.isVisible = true
                descriptionView.text = ""
                val diffUtil = KennelDiffUtil(volunteersList, kennelsList)
                val diffResult = DiffUtil.calculateDiff(diffUtil)
                kennelAdapter.kennels = kennelsList
                diffResult.dispatchUpdatesTo(kennelAdapter)
                if (kennelsList.size == 0) showDescriptionText(R.string.add_kennel_fragment_empty_kennels_text)
            }
        }

//        volunteersBtn.setOnClickListener {
//            val kennelsUnderscoreVisibility = kennelsUnderscore.isVisible
//            addKennelBtn.visibility = View.GONE
//            if (kennelsUnderscoreVisibility) {
//                kennelsUnderscore.isVisible = false
//                volunteersUnderscore.isVisible = true
//                descriptionView.text = ""
//                val diffUtil = KennelDiffUtil(kennelsList, volunteersList)
//                val diffResult = DiffUtil.calculateDiff(diffUtil)
//                kennelAdapter.kennels = volunteersList
//                diffResult.dispatchUpdatesTo(kennelAdapter)
//                if (volunteersList.size == 0)
//                    showDescriptionText(R.string.add_kennel_fragment_empty_volunteers_text)
//            }
//        }

        addKennelBtn.setOnClickListener {
            navigator.moveToKennelSettings()
        }
    }

    private suspend fun getKennels(personId: Long) = withContext(Dispatchers.IO) {
        val token = userPropertiesRepository.getUserToken()
        val kennelResponsePreview = networkKennelRepository.getKennelsByPersonId(token, personId)
        kennelResponsePreview.forEach {
            val kennelPreview = KennelPreview(
                it.kennelId,
                it.volunteersAmount,
                it.animalsAmount,
                it.name,
                it.avatarUrl,
                it.isValid
            )
            kennelsList.add(kennelPreview)
            isReadyKennelsList = true
        }
    }

    private fun initRecyclerView(view: View) {
        kennelRecycler =
            view.findViewById(R.id.add_kennel_fragment_kennels_or_volunteers_list)
        kennelAdapter = KennelsAdapter(
            kennelsList,
            object : KennelsAdapter.OnKennelItemClickListener {
                override fun onClick(kennelItem: KennelPreview) {
                    val kennel = bundleOf(KENNEL_ITEM_BUNDLE_KEY to kennelItem)
                    navigator.moveToKennelHome(kennel = kennel)
                }
            },
            networkImageLoader
        )
        kennelRecycler.adapter = kennelAdapter
        kennelRecycler.layoutManager = LinearLayoutManager(
            requireContext(), RecyclerView.VERTICAL, false
        )
    }

    private fun showDescriptionText(stringId: Int) {
        descriptionView.text = requireContext()
            .resources
            .getText(stringId)
    }

    private fun changeAnimationsWhenStartFragment(fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation) {
        when (fragmentsListForAssigningAnimation) {
            FragmentsListForAssigningAnimation.ANIMAL_LIST -> {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            }
            else -> {}
        }
    }

    private fun showError(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
    }
}