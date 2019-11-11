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
import com.nva.tpcell.models.Student
import com.nva.tpcell.utils.TPCellDatabase

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [StudentDetailsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [StudentDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudentDetailsFragment : Fragment() {

    private var studentData: Student? = null
    private var isUserAdmin: Boolean? = null
    private var listener: OnFragmentInteractionListener? = null
    var dbTPCellDatabase = TPCellDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isUserAdmin = it.getBoolean(IS_USER_ADMIN)
            studentData = it.getParcelable(STUDENT_OBJECT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.fragment_student_details, container, false)

        val studentName = inf.findViewById<EditText>(R.id.student_name)
        studentName.setText(studentData?.name)
        val studentEnroll = inf.findViewById<EditText>(R.id.student_enroll)
        studentEnroll.setText(studentData?.enroll)
        val studentEmail = inf.findViewById<EditText>(R.id.student_email)
        studentEmail.setText(studentData?.email)

        studentEmail.isEnabled = isUserAdmin == true

        val studentPhone = inf.findViewById<EditText>(R.id.student_phone)
        studentPhone.setText(studentData?.phone)
        val student10thAggregate = inf.findViewById<EditText>(R.id.student_10th_aggregate)
        student10thAggregate.setText(studentData?.aggregate_10th)
        val student12thAggregate = inf.findViewById<EditText>(R.id.student_12th_aggregate)
        student12thAggregate.setText(studentData?.aggregate_12th)
        val studentCollegeAggregate = inf.findViewById<EditText>(R.id.student_college_aggregate)
        studentCollegeAggregate.setText(studentData?.aggregate_college)

        val studentSubmitBtn = inf.findViewById<Button>(R.id.student_submit_button)
        studentSubmitBtn.setOnClickListener {
            // TODO if student not in database check
            val student = Student(
                studentEmail.text.toString(),
                studentName.text.toString(),
                studentEnroll.text.toString(),
                studentPhone.text.toString(),
                student10thAggregate.text.toString(),
                student12thAggregate.text.toString(),
                studentCollegeAggregate.text.toString()
            )
            dbTPCellDatabase.addStudentObject(context, student)
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
        const val STUDENT_OBJECT = "student-object"

        @JvmStatic
        fun newInstance(isUserAdmin: Boolean, studentData: Student) =
            StudentDetailsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_USER_ADMIN, isUserAdmin)
                    putParcelable(STUDENT_OBJECT, studentData)
                }
            }
    }
}
