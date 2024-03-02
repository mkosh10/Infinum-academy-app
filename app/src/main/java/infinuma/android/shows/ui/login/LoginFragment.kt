package infinuma.android.shows.ui.login

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color.alpha
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import infinuma.android.shows.databinding.FragmentLoginBinding
import infinuma.android.shows.networking.ApiModule

/* Put the app in the background and move it back to foreground
onCreate()
onStart()
onResume()
---
onPause()
onStart()
onResume()

Kill the app
onPause()
onStop()
onDestroy()

Lock the phones screen and unlock it
onCreate()
onStart()
onResume()
---
onPause()
onStart()
onResume()
 */

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    var boolPass = false
    var boolEmail = false

    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: LoginViewModel by viewModels()

    companion object {
        const val minPassLength = 6
        const val KEY_USERNAME = "EXTRA_USERNAME"
        const val REMEMBER_ME_CHECK_BOX = "REMEMBER_ME_CHECK_BOX"
        const val REMEMBER_EMAIL = "REMEMBER_EMAIL"
        const val SHOWS_SHARED_PREFERENCES = "Shows"
        const val IS_REGISTRATION_SUCCESSFUL = "IS_REGISTRATION_SUCCESSFUL"
        const val ANIM_1F = 1f
        const val ANIM_0F = 0f
        const val ANIM_NEG_1000F = -1000f

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(SHOWS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isRegistrationSuccessful()
        if (sharedPreferences.getBoolean(REMEMBER_ME_CHECK_BOX, false)) {
            val directions =
                LoginFragmentDirections.toShowsFragment(sharedPreferences.getString(REMEMBER_EMAIL, "REMEMBER_EMAIL").toString())
            findNavController().navigate(directions)
        } else {
            binding.triangleImg.animLogo()
        }
        viewModel.initSessionManager(requireContext())
        apiObserveLoginData()
        initListeners()
    }

    private fun initListeners() {

        binding.loginButtonId.setOnClickListener {
            viewModel.onLoginButtonClicked(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }

        binding.registerButtonId.setOnClickListener {
            val directions = LoginFragmentDirections.fromLoginToRegisterFragment()
            findNavController().navigate(directions)
        }

        binding.emailEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                val emailEntered = s.toString()
                boolEmail = Patterns.EMAIL_ADDRESS.matcher(emailEntered).matches()
                binding.loginButtonId.isEnabled = boolEmail && boolPass
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val passEntered = s.toString()
                boolPass = passEntered.length >= minPassLength
                binding.loginButtonId.isEnabled = boolEmail && boolPass
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
    }

    private fun View.animLogo() {
        alpha(1)
        translationY = ANIM_NEG_1000F
        animate()
            .alpha(ANIM_1F)
            .translationY(ANIM_0F)
            .setInterpolator(AccelerateInterpolator())
            .setDuration(500)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    binding.showsTitleId.animTitleShows()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }

            })
            .start()
    }

    fun View.animTitleShows() {
        alpha(0)
        scaleX = ANIM_0F
        scaleY = ANIM_0F
        animate()
            .alpha(ANIM_1F)
            .scaleX(ANIM_1F)
            .scaleY(ANIM_1F)
            .setInterpolator(BounceInterpolator())
            .setDuration(1000)
            .start()
    }

    private fun isRegistrationSuccessful() {
        if (sharedPreferences.getBoolean(IS_REGISTRATION_SUCCESSFUL, false)) {
            binding.registerButtonId.isVisible = false
            binding.loginText.isVisible = false
            binding.registrationSuccessfulText.isVisible = true
            sharedPreferences.edit() {
                putBoolean(IS_REGISTRATION_SUCCESSFUL, false)
            }
        }
    }

    private fun apiObserveLoginData() {
        viewModel.loginResultLiveData.observe(viewLifecycleOwner) { isSuccessful ->
            Log.e("LoginFragment", "$isSuccessful")
            if (isSuccessful) {
                val directions = LoginFragmentDirections.toShowsFragment(binding.emailEditText.text!!.toString())
                if (binding.checkBox.isChecked) {
                    sharedPreferences.edit {
                        putBoolean(REMEMBER_ME_CHECK_BOX, true)
                        putString(REMEMBER_EMAIL, binding.emailEditText.text!!.toString())
                    }
                }
                findNavController().navigate(directions)
            } else {
                Toast.makeText(requireContext(), "Login was not successful!", Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(), "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        Log.e("LoginActivity", "onPause called !")
    }

    override fun onStart() {
        super.onStart()
        Log.e("LoginActivity", "onStart() called !")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("LoginActivity", "onDestroy called !")
    }

    override fun onResume() {
        super.onResume()
        Log.v("LoginActivity", "onResume() called !")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}