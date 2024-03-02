package infinuma.android.shows.ui.shows

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.R
import infinuma.android.shows.db.ShowViewModelFactory
import infinuma.android.shows.ShowsApplication
import infinuma.android.shows.databinding.DialogUserProfileBinding
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.module.FileUtil
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.ui.InternetConnection
import infinuma.android.shows.ui.login.LoginFragment
import java.io.File

class ShowsFragment : Fragment() {

    private val args by navArgs<ShowsFragmentArgs>()
    private var _binding: FragmentShowsBinding? = null
    private val binding get() = _binding!!

    //    private val viewModel by viewModels<ShowsViewModel>()
    private val viewModel: ShowsViewModel by viewModels {
        ShowViewModelFactory((requireActivity().application as ShowsApplication).database)
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ShowsAdapter

    companion object {
        const val SHOWS_SHARED_PREFERENCES = "Shows"
    }

    val getContentval = registerForActivityResult<Uri, Boolean>(
        ActivityResultContracts.TakePicture()
    ) { pictureTaken ->

        if (pictureTaken) {
            var file = FileUtil.getImageFile(requireContext())
            var uri = FileProvider.getUriForFile(requireContext(), requireActivity().packageName + ".provider", file!!)
            sharedPreferences.edit {
                putString(args.username, uri.toString())
            }
            binding.userProfile.setImageURI(uri)
            viewModel.putUserImage(file)
        } else {
            Log.e("ShowsFragment", "PICTURE WASN'T TAKEN")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(SHOWS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ApiModule.initRetrofit(requireContext())
        checkConnectivity()
        setData()
        setProfilePhoto()
        initShowRecycler()
        initListeners()

    }

    private fun setData() {
        viewModel.checkConnectivity.observe(viewLifecycleOwner) { internetConnection ->
            if (internetConnection) {
                viewModel.listShowList()
            } else {
                viewModel.showsListLiveData.observe(viewLifecycleOwner) { showsList ->
                    if (showsList.size == 0) {
                        binding.emptyShowsView.isVisible = true
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.userProfile.setOnClickListener {
            profileBottomSheetDialog()
        }
    }

    private fun initShowRecycler() {

        viewModel.showsListLiveData.observe(viewLifecycleOwner) { showsList ->
            adapter = ShowsAdapter(showsList) { show ->
                Log.e("ShowsFragment", "FROM SHOWS TO SHOWDETAILS")
                val directions = ShowsFragmentDirections.toShowDetailsFragment(args.username, show)
                findNavController().navigate(directions)
            }
            binding.recyclerViewShows.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewShows.adapter = adapter
        }

    }

    private fun setProfilePhoto() {
        viewModel.getProfilePictureViewModel()
        viewModel.usersMELiveData.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(binding.userProfile)
        }
    }


    private fun checkConnectivity() {
        val isDeviceOnline = InternetConnection.isOnline(requireContext())
        viewModel.checkConnectivity(isDeviceOnline)
    }


    private fun profileBottomSheetDialog(): BottomSheetDialog {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogUserProfileBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.changeProfilePhoto.setStrokeColorResource(R.color.purpleColor_2)
        dialogBinding.changeProfilePhoto.setStrokeWidthResource(R.dimen._2dp)
        dialogBinding.userEmail.text = args.username

        viewModel.getProfilePictureViewModel()
        viewModel.usersMELiveData.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(dialogBinding.userProfilePicture)
        }


        dialogBinding.logoutButton.setOnClickListener {
            dialog.dismiss()
            logoutAlertDialog()
        }

        dialogBinding.changeProfilePhoto.setOnClickListener {
            val file: File? = FileUtil.createImageFile(requireContext())
            file?.let { img -> viewModel.putUserImage(img) }
            val uri = FileProvider.getUriForFile(requireContext(), requireActivity().packageName + ".provider", file!!)
            dialogBinding.userProfilePicture
            getContentval.launch(uri)
            dialog.dismiss()
        }
        dialog.show()
        return dialog
    }

    private fun logoutAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.log_out)
            .setMessage(R.string.areUSureUWantToLogOut)
            .setNegativeButton(R.string.No) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.dismiss()
                sharedPreferences.edit {
                    putBoolean(LoginFragment.REMEMBER_ME_CHECK_BOX, false)
                }
                val directions = ShowsFragmentDirections.fromShowsTologinFragment()
                findNavController().navigate(directions)
            }
        builder.show()
    }

    private fun changeProfilePhotoAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.ChangeProfilePhoto)
            .setMessage(R.string.fromGalleryOrTakeAPicture)
            .setNegativeButton(R.string.Galleryy) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.TakeAPicture) { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}