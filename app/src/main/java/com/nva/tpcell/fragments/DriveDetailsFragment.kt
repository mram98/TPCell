package com.nva.tpcell.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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
    private var dbTPCellDatabase = TPCellDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            isUserAdmin = it.getBoolean(IS_USER_ADMIN)
            driveData = it.getParcelable(DRIVE_OBJECT)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.title = "${driveData?.name}"

        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.fragment_drive_details, container, false)

        val driveName = inf.findViewById<EditText>(R.id.drive_name)
        driveName.setText(driveData?.name)
        val driveDesc = inf.findViewById<EditText>(R.id.drive_desc)
        driveDesc.setText(driveData?.desc)
        val drive10thAggregate = inf.findViewById<EditText>(R.id.drive_10th_aggregate)
        drive10thAggregate.setText(driveData?.aggregate_10th.toString())
        val drive12thAggregate = inf.findViewById<EditText>(R.id.drive_12th_aggregate)
        drive12thAggregate.setText(driveData?.aggregate_12th.toString())
        val driveCollegeAggregate = inf.findViewById<EditText>(R.id.drive_college_aggregate)
        driveCollegeAggregate.setText(driveData?.aggregate_college.toString())

        val driveSubmitBtn = inf.findViewById<Button>(R.id.drive_submit_button)
        driveSubmitBtn.setOnClickListener {
            // TODO Dialog box if drive already in database, do you want to replace
            val drive = Drive(
                driveName.text.toString(),
                driveDesc.text.toString(),
                drive10thAggregate.text.toString().toInt(),
                drive12thAggregate.text.toString().toInt(),
                driveCollegeAggregate.text.toString().toInt()
            )
            dbTPCellDatabase.addDriveObject(context, drive)
        }
        val driveEligibleBtn = inf.findViewById<Button>(R.id.drive_eligible_button)
        // if driveData is empty, then disable Eligible button
        if ((driveData?.name == "") || (driveData?.name == null)) {
            driveEligibleBtn.isEnabled = false
        } else {
            driveEligibleBtn.setOnClickListener {
                // View Eligible Students
                val studentsFragment =
                    StudentsFragment.newInstance(isUserAdmin!!, driveData?.name)
                fragmentManager!!
                    .beginTransaction()
                    .replace(R.id.container, studentsFragment)
                    .addToBackStack(studentsFragment.toString())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
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

//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }

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
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isUserAdmin == true) {
            inflater.inflate(R.menu.details, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when (item.itemId) {
            R.id.action_delete -> {
                dbTPCellDatabase.deleteDrive(context, driveData?.name)
                return true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
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
