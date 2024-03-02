package infinuma.android.shows.ui.show_details

import android.content.Context
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.R
import infinuma.android.shows.ShowsApplication
import infinuma.android.shows.databinding.DialogAddReviewBinding
import infinuma.android.shows.databinding.FragmentShowDetailsBinding
import infinuma.android.shows.db.ShowViewModelFactory
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.ui.InternetConnection
import infinuma.android.shows.ui.login.LoginFragment.Companion.SHOWS_SHARED_PREFERENCES

class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null
    private val args by navArgs<ShowDetailsFragmentArgs>()

    private val binding get() = _binding!!
    private val viewModel: ShowDetailsViewModel by viewModels {
        ShowViewModelFactory((requireActivity().application as ShowsApplication).database)
    }
    private lateinit var adapterReviews: ReviewsAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(SHOWS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        viewModel.showsId.value = args.clickedShow.id
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkConnectivity()
        setData()
        initReviewRecycler()
        backArrowColor()
        initListeners()
        writeAReview()
        showImageShowDesc()
        setShowRating()
    }

    private fun checkConnectivity() {
        viewModel.checkConnectivity(InternetConnection.isOnline(requireContext()))
    }

    private fun setData() {
        viewModel.checkConnectivity.observe(viewLifecycleOwner) { internetConnection ->
            if (internetConnection) {
                Log.e("ShowDetailsFragment", "INTERNET CONNECTION")
                viewModel.listOfReviewsForShowWithId(args.clickedShow.id)
            } else {
                Log.e("ShowDetailsFragment", "NO INTERNET CONNECTION")

                viewModel.listSizeLiveData.observe(viewLifecycleOwner) { listSize ->
                    if (listSize == 0) {
                        binding.noReviewsYet.isVisible = true
                    } else {
                        binding.ratingBar.rating = args.clickedShow.averageRating!!

                        binding.averageRatingText.text =
                            String.format("%s REVIEWS %.2f AVERAGE", viewModel.listSizeLiveData.value, args.clickedShow.averageRating)
                    }
                }
            }
        }
    }

    private fun showImageShowDesc() {
        context?.let {
            Glide.with(it)
                .load(args.clickedShow.showImageUrl)
                .apply(RequestOptions.fitCenterTransform())
                .placeholder(R.drawable.loading_image)
                .into(binding.imageDetailsActivity)
        }
        binding.showDesc.text = args.clickedShow.description.toString()
        binding.collapsingToolbar.title = args.clickedShow.title
    }

    private fun backArrowColor() {
        val colorBlack = ContextCompat.getColor(requireContext(), android.R.color.black)
        binding.toolbarId.navigationIcon?.mutate()?.setColorFilter(colorBlack, PorterDuff.Mode.SRC_ATOP)
    }

    private fun initListeners() {
        binding.toolbarId.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun initReviewRecycler() {

        adapterReviews = ReviewsAdapter(emptyList()) { review ->

        }
        binding.recyclerViewDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDetails.adapter = adapterReviews
        binding.recyclerViewDetails.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        viewModel.reviewsListLiveData.observe(viewLifecycleOwner) { reviewsListLive ->
            adapterReviews.newItemList(reviewsListLive)
        }

    }

    private fun writeAReview() {
        binding.writeReviewButton.setOnClickListener {
            bottomSheetDialog()
        }
    }

    private fun setShowRating() {
        viewModel.averageReviewLiveData.observe(viewLifecycleOwner) { averageRev ->
            binding.ratingBar.rating = averageRev

            //            binding.averageRatingText.text = buildString {
            //                append(viewModel.listSizeLiveData.value)
            //                append(" reviews, ")
            //                append(String.format("% .2f", averageRev))
            //                append(" AVERAGE")
            //            }
            binding.averageRatingText.text = String.format("%s REVIEWS %.2f AVERAGE", viewModel.listSizeLiveData.value, averageRev)
        }
    }

    private fun bottomSheetDialog(): BottomSheetDialog {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.rateStars.setOnRatingBarChangeListener { _, rating, _ ->
            viewModel.checkRating(rating)
        }
        viewModel.ratingLiveData.observe(dialog) {
            dialogBinding.addReviewButtonId.isEnabled = it
        }

        dialogBinding.addReviewButtonId.setOnClickListener {
            viewModel.onSubmitReviewButtonClicked(
                dialogBinding.rateStars.rating.toInt(),
                dialogBinding.writeSomething.text.toString(),
                args.clickedShow.id.toInt()
            )
            viewModel.listOfReviewsForShowWithId(args.clickedShow.id)
            //            setShowRating()
            dialog.dismiss()
        }
        dialog.show()
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}