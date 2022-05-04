package com.absinthe.libchecker.ui.fragment.detail

import android.content.DialogInterface
import android.text.method.LinkMovementMethod
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.absinthe.libchecker.R
import com.absinthe.libchecker.annotation.LibType
import com.absinthe.libchecker.annotation.NATIVE
import com.absinthe.libchecker.base.BaseBottomSheetViewDialogFragment
import com.absinthe.libchecker.utils.extensions.putArguments
import com.absinthe.libchecker.view.app.BottomSheetHeaderView
import com.absinthe.libchecker.view.detail.LibDetailBottomSheetView
import com.absinthe.libchecker.viewmodel.DetailViewModel
import com.absinthe.rulesbundle.LCRules
import kotlinx.coroutines.launch

const val EXTRA_LIB_NAME = "EXTRA_LIB_NAME"
const val EXTRA_LIB_TYPE = "EXTRA_LIB_TYPE"
const val EXTRA_REGEX_NAME = "EXTRA_REGEX_NAME"

class LibDetailDialogFragment : BaseBottomSheetViewDialogFragment<LibDetailBottomSheetView>() {

  private val libName by lazy { arguments?.getString(EXTRA_LIB_NAME).orEmpty() }
  private val type by lazy { arguments?.getInt(EXTRA_LIB_TYPE) ?: NATIVE }
  private val regexName by lazy { arguments?.getString(EXTRA_REGEX_NAME) }
  private val viewModel: DetailViewModel by activityViewModels()
  private var isStickyEventReceived = false

  override fun initRootView(): LibDetailBottomSheetView = LibDetailBottomSheetView(requireContext())

  override fun init() {
    root.apply {
      title.text = libName
      lifecycleScope.launch {
        val iconRes = LCRules.getRule(libName, type, true)?.iconRes ?: com.absinthe.lc.rulesbundle.R.drawable.ic_sdk_placeholder
        icon.load(iconRes) {
          crossfade(true)
          placeholder(R.drawable.ic_logo)
        }
      }
    }
  }

  override fun getHeaderView(): BottomSheetHeaderView = root.getHeaderView()

  private fun List<String>.toContributorsString(): String {
    return this.joinToString(separator = ", ")
  }

  override fun onStart() {
    super.onStart()
    viewModel.detailBean.observe(viewLifecycleOwner) {
      if (it != null) {
        root.apply {
          libDetailContentView.apply {
            label.text.text = it.label
            team.text.text = it.team
            contributor.text.text = it.contributors.toContributorsString()
            description.text.text = it.description
            relativeLink.text.apply {
              isClickable = true
              movementMethod = LinkMovementMethod.getInstance()
              text = HtmlCompat.fromHtml(
                "<a href='${it.relativeUrl}'> ${it.relativeUrl} </a>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
              )
            }
          }

          root.showContent()
        }
      } else {
        if (isStickyEventReceived) {
          root.showNotFound()
        } else {
          isStickyEventReceived = true
        }
      }
    }
    if (regexName.isNullOrEmpty()) {
      viewModel.requestLibDetail(libName, type)
    } else {
      viewModel.requestLibDetail(regexName!!, type, true)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    viewModel.detailBean.value = null
  }

  override fun show(manager: FragmentManager, tag: String?) {
    if (!isShowing) {
      isShowing = true
      super.show(manager, tag)
    }
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    isShowing = false
  }

  companion object {
    fun newInstance(
      libName: String,
      @LibType type: Int,
      regexName: String? = null
    ): LibDetailDialogFragment {
      return LibDetailDialogFragment().putArguments(
        EXTRA_LIB_NAME to libName,
        EXTRA_LIB_TYPE to type,
        EXTRA_REGEX_NAME to regexName
      )
    }

    var isShowing = false
  }
}
