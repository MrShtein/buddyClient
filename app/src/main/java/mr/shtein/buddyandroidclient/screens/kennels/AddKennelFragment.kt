package mr.shtein.buddyandroidclient.screens.kennels

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis
import com.google.gson.Gson
import kotlinx.coroutines.*
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.adapters.KennelsAdapter
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.model.KennelPreview
import mr.shtein.network.NetworkService
import mr.shtein.buddyandroidclient.setInsetsListenerForPadding
import mr.shtein.buddyandroidclient.setStatusBarColor
import mr.shtein.buddyandroidclient.utils.KennelDiffUtil
import mr.shtein.buddyandroidclient.utils.FragmentsListForAssigningAnimation
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import org.koin.android.ext.android.inject

private const val ANIMAL_LIST_ID = "animalsListFragment"
private const val LAST_FRAGMENT_KEY = "last_fragment"
private const val KENNEL_SETTINGS_LABEL = "KennelSettingsFragment"
private const val KENNEL_HOME_LABEL = "KennelHomeFragment"


class AddKennelFragment : Fragment(R.layout.add_kennel_fragment) {

    companion object {
        private const val KENNEL_ITEM_BUNDLE_KEY = "kennel_item_key"
    }

    private lateinit var kennelsBtn: MaterialButton
    private lateinit var volunteersBtn: MaterialButton
    private lateinit var kennelsUnderscore: View
    private lateinit var volunteersUnderscore: View
    private lateinit var descriptionView: TextView
    private lateinit var storage: SharedPreferences
    private lateinit var addKennelBtn: MaterialButton
    private lateinit var kennelRecycler: RecyclerView
    private lateinit var kennelAdapter: KennelsAdapter
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var kennelsList = mutableListOf<KennelPreview>()
    private var isReadyKennelsList = false
    private var volunteersList = mutableListOf<KennelPreview>()
    private val networkService: NetworkService by inject()
    private val userPropertiesRepository: UserPropertiesRepository by inject()

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
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.label == KENNEL_SETTINGS_LABEL || destination.label == KENNEL_HOME_LABEL) {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            }
        }
        val fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation? = arguments?.getParcelable(LAST_FRAGMENT_KEY)
        if (fragmentsListForAssigningAnimation != null) {
            changeAnimationsWhenStartFragment(fragmentsListForAssigningAnimation)
        }
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.let {
            setStatusBarColor(true)
            setInsetsListenerForPadding(it, left = false, top = true, right = false, bottom = false)
            initViews(it)
            setListeners(it)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coroutineScope.launch {
            val personId = userPropertiesRepository.getUserId()
            getKennels(personId)
            initRecyclerView(view)
            if (kennelsList.size == 0) {
                showDescriptionText(R.string.add_kennel_fragment_empty_kennels_text)
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
        kennelsBtn = view.findViewById(R.id.add_kennel_fragment_kennels_btn)
        volunteersBtn = view.findViewById(R.id.add_kennel_fragment_volunteers_btn)
        kennelsUnderscore = view.findViewById(R.id.add_kennel_fragment_kennel_underscore)
        volunteersUnderscore = view.findViewById(R.id.add_kennel_fragment_volunteer_underscore)
        descriptionView = view.findViewById(R.id.add_kennel_fragment_kennels_or_volunteers_absence)
        addKennelBtn = view.findViewById(R.id.add_kennel_fragment_add_btn)
    }

    private fun setListeners(view: View) {
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

        volunteersBtn.setOnClickListener {
            val kennelsUnderscoreVisibility = kennelsUnderscore.isVisible
            addKennelBtn.visibility = View.GONE
            if (kennelsUnderscoreVisibility) {
                kennelsUnderscore.isVisible = false
                volunteersUnderscore.isVisible = true
                descriptionView.text = ""
                val diffUtil = KennelDiffUtil(kennelsList, volunteersList)
                val diffResult = DiffUtil.calculateDiff(diffUtil)
                kennelAdapter.kennels = volunteersList
                diffResult.dispatchUpdatesTo(kennelAdapter)
                if (volunteersList.size == 0)
                    showDescriptionText(R.string.add_kennel_fragment_empty_volunteers_text)
            }
        }

        addKennelBtn.setOnClickListener {
            findNavController().navigate(R.id.action_addKennelFragment_to_kennelSettingsFragment)
        }
    }

    private suspend fun getKennels(personId: Long) = withContext(Dispatchers.IO) {
        val token = userPropertiesRepository.getUserToken()
        val response = networkService.getKennelsByPersonId(token, personId)
        if (response.isSuccessful) {
            val kennelResponsePreview = response.body()
            kennelResponsePreview?.forEach {
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
        } else {
            when (response.code()) {
                403 -> goToLogin()
                else -> TODO()
            }
        }
    }

    private fun goToLogin() {
        Log.i("info", "Токен протух, необходимо его обновить")
        TODO()
    }

    private fun initRecyclerView(view: View) {
        kennelRecycler =
            view.findViewById(R.id.add_kennel_fragment_kennels_or_volunteers_list)
        kennelAdapter = KennelsAdapter(
            userPropertiesRepository.getUserToken(),
            kennelsList,
            object : KennelsAdapter.OnKennelItemClickListener {
                override fun onClick(kennelItem: KennelPreview) {
                    val bundle = bundleOf(KENNEL_ITEM_BUNDLE_KEY to kennelItem)
                    findNavController().navigate(
                        R.id.action_addKennelFragment_to_kennelHomeFragment,
                        bundle
                    )
                }
            }
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
}