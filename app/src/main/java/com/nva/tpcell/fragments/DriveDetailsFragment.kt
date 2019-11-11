package com.nva.tpcell.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.nva.tpcell.R
import com.nva.tpcell.models.Drive
import com.nva.tpcell.utils.TPCellDatabase

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DriveDetailsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DriveDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DriveDetailsFragment : Fragment() {

    private var driveData: Drive? = null
    private var isUserAdmin: Boolean? = null
    private var listener: OnFragmentInteractionListener? = null
    var dbTPCellDatabase = TPCellDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isUserAdmin = it.getBoolean(IS_USER_ADMIN)
            driveData = it.getParcelable(DRIVE_OBJECT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.fragment_drive_details, container, false)

        val driveName = inf.findViewById<EditText>(R.id.drive_name)
        driveName.setText(driveData?.name)
        val driveDesc = inf.findViewById<EditText>(R.id.drive_desc)
        driveDesc.setText(driveData?.desc)
        val drive10thAggregate = inf.findViewById<EditText>(R.id.drive_10th_aggregate)
        drive10thAggregate.setText(driveData?.aggregate_10th)
        val drive12thAggregate = inf.findViewById<EditText>(R.id.drive_12th_aggregate)
        drive12thAggregate.setText(driveData?.aggregate_12th)
        val driveCollegeAggregate = inf.findViewById<EditText>(R.id.drive_college_aggregate)
        driveCollegeAggregate.setText(driveData?.aggregate_college)

        val driveSubmitBtn = inf.findViewById<Button>(R.id.drive_submit_button)
        driveSubmitBtn.setOnClickListener {
            // TODO if drive not in database check
            val drive = Drive(
                driveName.text.toString(),
                driveDesc.text.toString(),
                drive10thAggregate.text.toString(),
                drive12thAggregate.text.toString(),
                driveCollegeAggregate.text.toString()
            )
            dbTPCellDatabase.addDriveObject(context, drive)
        }
        val driveEligibleBtn = inf.findViewById<Button>(R.id.drive_eligible_button)
        driveEligibleBtn.setOnClickListener {

            // TODO View Eligible Students
        }

        // If user is not admin, make everything non editable
        if (isUserAdmin == false) {
            driveSubmitBtn.visibility = View.GONE
            driveEligibleBtn.visibility = View.GONE
            driveName.isEnabled = false
            driveDesc.isEnabled = false
            drive10thAggregate.isEnabled = false
            drive12thAggregate.isEnabled = false
            driveCollegeAggregate.isEnabled = false
        }
        return inf
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        const val IS_USER_ADMIN = "is-user-admin"
        const val DRIVE_OBJECT = "drive-object"

        @JvmStatic
        fun newInstance(isUserAdmin: Boolean, driveData: Drive) =
            DriveDetailsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_USER_ADMIN, isUserAdmin)
                    putParcelable(DRIVE_OBJECT, driveData)
                }
            }
    }
}
