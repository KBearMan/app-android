package org.coepi.android.ui.symptoms.fever

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.coepi.android.databinding.FragmentFeverTemperatureSpotBinding.inflate
import org.coepi.android.ui.common.KeyboardHider
import org.coepi.android.ui.extensions.onBack
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeverTemperatureSpotFragment : Fragment() {

    private val viewModel by viewModel<FeverTemperatureSpotViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        onBack { viewModel.onBack() }
        toolbar.setNavigationOnClickListener { viewModel.onBackPressed() }
    }.root

    override fun onStop() {
        KeyboardHider().hideKeyboard(this.requireContext(), this.requireView())
        super.onStop()
    }
}