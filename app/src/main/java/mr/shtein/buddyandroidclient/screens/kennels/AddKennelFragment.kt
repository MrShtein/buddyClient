package mr.shtein.buddyandroidclient.screens.kennels

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.coroutines.*
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.adapters.KennelsAdapter
import mr.shtein.buddyandroidclient.model.KennelPreview
import mr.shtein.buddyandroidclient.model.VolunteersPreview
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.utils.KennelDiffUtil
import mr.shtein.buddyandroidclient.utils.SharedPreferences

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        coroutineScope.launch {
            val personId = storage.readLong(SharedPreferences.USER_ID_KEY, 0)
            getKennels(personId)
            initRecyclerView(view)
            if (kennelsList.size == 0) {
                showDescriptionText(R.string.add_kennel_fragment_empty_kennels_text)
            }
        }
        setListeners(view)
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
        storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
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
            if (volunteersUnderscoreVisibility) {
                volunteersUnderscore.isVisible = false
                kennelsUnderscore.isVisible = true
                descriptionView.text = ""
            }
            val diffUtil = KennelDiffUtil(volunteersList, kennelsList)
            val diffResult = DiffUtil.calculateDiff(diffUtil)
            kennelAdapter.kennels = kennelsList
            diffResult.dispatchUpdatesTo(kennelAdapter)
            if (kennelsList.size == 0) showDescriptionText(R.string.add_kennel_fragment_empty_kennels_text)
        }

        volunteersBtn.setOnClickListener {
            val kennelsUnderscoreVisibility = kennelsUnderscore.isVisible
            if (kennelsUnderscoreVisibility) {
                kennelsUnderscore.isVisible = false
                volunteersUnderscore.isVisible = true
                descriptionView.text = ""
            }
            val diffUtil = KennelDiffUtil(kennelsList, volunteersList)
            val diffResult = DiffUtil.calculateDiff(diffUtil)
            kennelAdapter.kennels = volunteersList
            diffResult.dispatchUpdatesTo(kennelAdapter)
            if (volunteersList.size == 0)
                showDescriptionText(R.string.add_kennel_fragment_empty_volunteers_text)
        }

        addKennelBtn.setOnClickListener {
            findNavController().navigate(R.id.action_addKennelFragment_to_kennelSettingsFragment)
        }
    }

    private suspend fun getKennels(personId: Long) = withContext(Dispatchers.IO) {
        val token = "Bearer ${storage.readString(SharedPreferences.TOKEN_KEY, "")}"
        val retrofit = Common.retrofitService
        val response = retrofit.getKennelsByPersonId(token, personId)
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
            storage.readString(SharedPreferences.TOKEN_KEY, ""),
            kennelsList,
            object : KennelsAdapter.OnKennelItemClickListener {
                override fun onClick(kennelItem: KennelPreview) {
                    val gson = Gson()
                    val kennelItemJson = gson.toJson(kennelItem)
                    val bundle = bundleOf(KENNEL_ITEM_BUNDLE_KEY to kennelItemJson)
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
}