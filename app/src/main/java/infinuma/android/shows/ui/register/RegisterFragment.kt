package infinuma.android.shows.ui.register

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import infinuma.android.shows.databinding.FragmentRegisterBinding
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.ui.login.LoginFragment
import infinuma.android.shows.ui.login.LoginFragment.Companion.IS_REGISTRATION_SUCCESSFUL
import infinuma.android.shows.ui.login.LoginFragment.Companion.SHOWS_SHARED_PREFERENCES

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    var boolPass = false
    var boolEmail = false
    var boolRepeatPass = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(SHOWS_SHARED_PREFERENCES, Context.MODE_PRIVATE)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        registerButtonClicked()
        initListeners()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.registrationResultLiveData.observe(viewLifecycleOwner) { isSuccessful ->
            Log.e("RegisterFragment", "$isSuccessful")
            sharedPreferences.edit() {
                putBoolean(IS_REGISTRATION_SUCCESSFUL, isSuccessful)
            }
            if (isSuccessful) {
                Log.e("RegisterFragment", "REGISTRATION WAS SUCCESSFUL")
                Toast.makeText(requireContext(), "Registration was successful :)", Toast.LENGTH_SHORT).show()
                val directions = RegisterFragmentDirections.fromRegisterToLoginFragment()
                findNavController().navigate(directions)
            } else {
                Log.e("RegisterFragment", "REGISTRATION WAS NOT SUCCESSFUL")
                Toast.makeText(requireContext(), "Registration was not successful!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerButtonClicked() {
        binding.registerButtonId.setOnClickListener {
            viewModel.onRegisterButtonClicked(
                username = binding.emailEditText.text.toString(),
                password = binding.passwordEditText.text.toString(),
                passwordConfirmation = binding.repeatPasswordEditText.text.toString()
            )
        }
    }

    private fun initListeners() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                val emailEntered = s.toString()
                boolEmail = Patterns.EMAIL_ADDRESS.matcher(emailEntered).matches()
                binding.registerButtonId.isEnabled = boolEmail && boolPass && boolRepeatPass
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
                boolPass = passEntered.length >= LoginFragment.minPassLength
                binding.registerButtonId.isEnabled = boolEmail && boolPass && boolRepeatPass
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


        binding.repeatPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val repeatPassEntered = s.toString()
                boolRepeatPass = repeatPassEntered == binding.passwordEditText.text.toString()
                binding.registerButtonId.isEnabled = boolEmail && boolPass && boolRepeatPass
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}