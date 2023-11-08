package com.branchx.agent.ui.fragment.auth

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.branchx.agent.R
import com.branchx.agent.data.response.City
import com.branchx.agent.data.response.State
import com.branchx.agent.databinding.FragmentRegisterBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.*
import com.branchx.agent.helper.util.AppUtil
import com.branchx.agent.helper.util.ImagePickerFragment
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.auth.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(R.layout.fragment_register) {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var imagePicker: ImagePickerFragment
    private var imagePicType = 0

    private var stateSpinnerDialog: SpinnerDialog? = null
    private var citySpinnerDialog: SpinnerDialog? = null
    private var registeringUserDialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        picImage()
        imagePicker = ImagePickerFragment(this)

        viewModel.fetchCityList()
        subscribeObservers()
        binding.let {
            it.llState.setOnClickListener {
                stateSpinnerDialog?.showSpinerDialog()
            }
            it.llCity.setOnClickListener {
                citySpinnerDialog?.showSpinerDialog()
            }
        }

        binding.btnSubmit.setOnClickListener {
            //   viewModel.registerUser()
            viewModel.showuUser()
        }
        binding.tvAlreadyAccount.setOnClickListener {
            viewModel.showuUser()
        }
    }


    private fun subscribeObservers() {
        /*viewModel.stateListObserver.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    setVisibilityInMain(false)
                }
                is Resource.Failure -> {
                    setVisibilityInMain(false)
                }
                is Resource.Success -> {
                    setVisibilityInMain()
                    setupStateDialog(it.data.stateList)
                }
            }
        })*/

        viewModel.cityListObserver.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    setVisibilityInMain(false)
                }
                is Resource.Failure -> {
                    setVisibilityInMain(false)
                }
                is Resource.Success -> {
                    setVisibilityInMain()
                    setupCityDialog(it.data.cityList)
                }
            }
        })

        viewModel.registerUserObserver.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    registeringUserDialog =
                        AppDialog.progress(requireContext(), "Registering User...")
                }
                is Resource.Failure -> {
                    registeringUserDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
                is Resource.Success -> {
                    registeringUserDialog?.dismiss()
                    if (it.data.status == 1) {
                        AppDialog.success(requireActivity(), it.data.message).apply {
                            this.findViewById<Button>(R.id.btn_done).setOnClickListener {
                                this.dismiss()
                                onBackPressed()
                            }
                        }
                    }
                }
            }
        }

        viewModel.userListObserver.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    AppUtil.logger("User List: $it")
                }
                is Resource.Success -> {
                    AppUtil.logger("User List: $it")
                }
                is Resource.Failure -> {
                    AppUtil.logger("User List: $it")
                }
            }
        }

    }

    private fun setupStateDialog(data: List<State>) {

        val stateList= data.map { it.stateName } as ArrayList<String>

        stateSpinnerDialog = SpinnerDialog(
            requireActivity(),
            stateList,
            "Search State",
            "Close"
        )
        stateSpinnerDialog?.let { spinner ->
            spinner.setCancellable(true)
            spinner.setShowKeyboard(true)
            spinner.bindOnSpinerListener { item, _ ->
                binding.tvState.text = item
            }
        }
    }

    private fun setupCityDialog(data: List<City>) {

        val mmap = HashMap<String, String>()
        data.forEach {
            mmap[it.cityName] = it.cityId.toString()
        }
        val mList = ArrayList<String>()
        mmap.forEach {
            mList.add(it.key)
        }
        citySpinnerDialog = SpinnerDialog(
            requireActivity(),
            mList,
            "Search City",
            "Close"
        )

        citySpinnerDialog?.let { spinner ->
            spinner.setCancellable(true)
            spinner.setShowKeyboard(true)
            spinner.bindOnSpinerListener { item, _ ->
                binding.tvCity.text = item
                viewModel.cityId = mmap[item] ?: ""
            }
        }
    }

    private fun setVisibilityInMain(visibility: Boolean = true) {
        binding.progressLayoutMain
        val progress = binding.progressLayoutMain.root
        if (visibility) {
            progress.hide()
            binding.svMainLayout.show()
        } else {
            progress.show()
            binding.svMainLayout.hide()
        }
    }

    private fun picImage() {
        binding.let {
            //AADHAAR FRONT PIC
            it.llAadhaarFrontImage.setOnClickListener {
                imagePicType = IMAGE_PIC_AADHAAR_FRONT
                activity?.requestCameraStoragePermission {
                    imagePicker.openCameraGallery()
                }
            }
            //AADHAAR BACK PIC
            it.llAadhaarBackImage.setOnClickListener {
                imagePicType = IMAGE_PIC_AADHAAR_BACK
                activity?.requestCameraStoragePermission {
                    imagePicker.openCameraGallery()
                }
            }
            //PAN CARD PIC
            it.llPanCardImage.setOnClickListener {
                imagePicType = IMAGE_PIC_PAN_CARD
                activity?.requestCameraStoragePermission {
                    imagePicker.openCameraGallery()
                }
            }
            //PAN USER PROFILE PIC
            it.llProfilePic.setOnClickListener {
                imagePicType = IMAGE_PIC_USER_PROFILE
                activity?.requestCameraStoragePermission {
                    imagePicker.openCameraGallery()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        activity?.makeToast("called")
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImagePickerFragment.IMAGE_CAPTURE_CODE) {

                val imageView = when (imagePicType) {
                    IMAGE_PIC_AADHAAR_FRONT -> binding.ivAadhaarFront
                    IMAGE_PIC_AADHAAR_BACK -> binding.ivAadhaarBack
                    IMAGE_PIC_PAN_CARD -> binding.ivPanCard
                    IMAGE_PIC_USER_PROFILE -> binding.ivUserProfile
                    else -> null
                }

                var bitmap: Bitmap? = imagePicker.mfile.toBitmap()
                if (imagePicker.mfile.sizeInKb > 512)
                    bitmap = imagePicker.mfile.reduceFileSize()?.toBitmap()

                setImageFile(bitmap, imageView)

                when (imagePicType) {
                    IMAGE_PIC_AADHAAR_FRONT -> viewModel.aadhaarFrontBitmap = bitmap
                    IMAGE_PIC_AADHAAR_BACK -> viewModel.aadhaarBackBitmap = bitmap
                    IMAGE_PIC_PAN_CARD -> viewModel.panCardBitmap = bitmap
                    IMAGE_PIC_USER_PROFILE -> viewModel.profileBitmap = bitmap
                }

            } else if (requestCode == ImagePickerFragment.GALLERY_PIC_IMAGE_CODE) {
                binding.let {
                    when (imagePicType) {
                        IMAGE_PIC_AADHAAR_FRONT -> setImageLayoutVisible(
                            it.progressLayoutAadharFrontImage.rootLayout, false
                        )
                        IMAGE_PIC_AADHAAR_BACK -> setImageLayoutVisible(
                            it.progressLayoutAadharBackImage.rootLayout, false
                        )
                        IMAGE_PIC_PAN_CARD -> setImageLayoutVisible(
                            it.progressLayoutPanCardImage.rootLayout, false
                        )
                        IMAGE_PIC_USER_PROFILE -> setImageLayoutVisible(
                            it.progressLayoutProfileImage.rootLayout, false
                        )
                    }
                }

                lifecycleScope.launch(Dispatchers.Main) {
                    val bitmap = ImagePickerFragment.readBitmapFromInputStream(activity, data)
                    binding.let {
                        when (imagePicType) {
                            IMAGE_PIC_AADHAAR_FRONT -> {
                                setImageLayoutVisible(it.progressLayoutAadharFrontImage.rootLayout)
                                setImageFile(bitmap, it.ivAadhaarFront)
                                viewModel.aadhaarFrontBitmap = bitmap
                            }
                            IMAGE_PIC_AADHAAR_BACK -> {
                                setImageLayoutVisible(it.progressLayoutAadharBackImage.rootLayout)
                                setImageFile(bitmap, it.ivAadhaarBack)
                                viewModel.aadhaarBackBitmap = bitmap
                            }
                            IMAGE_PIC_PAN_CARD -> {

                                setImageLayoutVisible(it.progressLayoutPanCardImage.rootLayout)
                                setImageFile(bitmap, it.ivPanCard)
                                viewModel.panCardBitmap = bitmap
                            }
                            IMAGE_PIC_USER_PROFILE -> {

                                setImageLayoutVisible(it.progressLayoutProfileImage.rootLayout)
                                setImageFile(bitmap, it.ivUserProfile)

                                viewModel.profileBitmap = bitmap
                            }
                        }
                    }

                }
            }
        }

    }

    private fun setImageFile(bitmap: Bitmap?, imageView: ImageView?) {
        bitmap?.let { f ->
            imageView?.setImageBitmap(bitmap)
        } ?: activity?.makeToast("file is null! try again")

    }

    private fun setImageLayoutVisible(progressLayout: View, visibility: Boolean = true) {
        if (visibility) {

            binding.llAadhaarFrontImage.let { layout ->
                layout.alpha = 1f
                layout.isEnabled = true
                layout.isClickable = true
            }
            binding.llAadhaarBackImage.let { layout ->
                layout.alpha = 1f
                layout.isEnabled = true
                layout.isClickable = true
            }
            binding.llPanCardImage.let { layout ->
                layout.alpha = 1f
                layout.isEnabled = true
                layout.isClickable = true
            }
            binding.llProfilePic.let { layout ->
                layout.alpha = 1f
                layout.isEnabled = true
                layout.isClickable = true
            }

            progressLayout.hide()
        } else {
            binding.llAadhaarFrontImage.let { layout ->
                layout.alpha = 0.5f
                layout.isEnabled = false
                layout.isClickable = false
            }
            binding.llAadhaarBackImage.let { layout ->
                layout.alpha = 0.5f
                layout.isEnabled = false
                layout.isClickable = false
            }
            binding.llPanCardImage.let { layout ->
                layout.alpha = 0.5f
                layout.isEnabled = false
                layout.isClickable = false
            }
            binding.llProfilePic.let { layout ->
                layout.alpha = 0.5f
                layout.isEnabled = false
                layout.isClickable = false
            }
            progressLayout.show()
        }
    }

    companion object {
        const val IMAGE_PIC_AADHAAR_FRONT = 1
        const val IMAGE_PIC_AADHAAR_BACK = 2
        const val IMAGE_PIC_PAN_CARD = 3
        const val IMAGE_PIC_USER_PROFILE = 4

        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }
}