package com.nva.tpcell.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nva.tpcell.R
import com.nva.tpcell.models.Student
import com.nva.tpcell.utils.TPCellDatabase

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [StudentsFragment.OnListFragmentInteractionListener] interface.
 */
class StudentsFragment : Fragment() {

    private var isUserAdmin = false
    private var driveName: String? = null
    private var listener: OnListFragmentInteractionListener? = null
    private var adapterStudent: StudentFirestoreRecyclerAdapter? = null

    private lateinit var options: FirestoreRecyclerOptions<Student>
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabButton: FloatingActionButton

    private var dbTPCellDatabase: TPCellDatabase = TPCellDatabase()

    lateinit var studentDetailsFragment: StudentDetailsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            isUserAdmin = it.getBoolean(IS_USER_ADMIN)
            driveName = it.getString(DRIVE_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_students_list, container, false)

        // Getting Query and making Adapter Class
        val query = dbTPCellDatabase.getStudentsList(driveName)
        options =
            FirestoreRecyclerOptions.Builder<Student>().setQuery(query, Student::class.java).build()
        adapterStudent = StudentFirestoreRecyclerAdapter(options)

        // Binding Query to Adapter
        recyclerView = view.findViewById(R.id.students_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapterStudent

        // Setting up FAB button
        fabButton = view.findViewById(R.id.button_add_student)
        if (isUserAdmin) {
            (fabButton as View).visibility = View.VISIBLE
        } else {
            (fabButton as View).visibility = View.INVISIBLE
        }
        fabButton.setOnClickListener {
            // view ->
            // Open StudentDetailsFragment with no data
            studentDetailsFragment = StudentDetailsFragment.newInstance(isUserAdmin, Student())
            // Null Forced
            fragmentManager!!
                .beginTransaction()
                .replace(R.id.container, studentDetailsFragment)
                .addToBackStack(studentDetailsFragment.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    override fun onStart() {
        super.onStart()
        adapterStudent!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (adapterStudent != null) {
            adapterStudent!!.stopListening()
        }
    }

    private inner class StudentFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Student>) :
        FirestoreRecyclerAdapter<Student, StudentViewHolder>(options) {

        override fun onBindViewHolder(
            studentViewHolder: StudentViewHolder,
            position: Int,
            student: Student
        ) {
            studentViewHolder.setStudentName(student.name)
            studentViewHolder.setOnStudentItemClickListener(object : StudentItemClickListener {
                override fun onStudentItemClickListener(view: View, pos: Int) {
                    // Start StudentDetailsFragment with student obj as parcelable
                    studentDetailsFragment =
                        StudentDetailsFragment.newInstance(isUserAdmin, student)
                    // Null Forced
                    fragmentManager!!
                        .beginTransaction()
                        .replace(R.id.container, studentDetailsFragment)
                        .addToBackStack(studentDetailsFragment.toString())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_student_row, parent, false)

            return StudentViewHolder(view)
        }
    }

    private inner class StudentViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            studentItemClickListener!!.onStudentItemClickListener(p0!!, adapterPosition)
        }

        var studentItemClickListener: StudentItemClickListener? = null
        fun setOnStudentItemClickListener(itemClickListener: StudentItemClickListener) {
            studentItemClickListener = itemClickListener
        }

        internal fun setStudentName(studentName: String?) {
            val textView = view.findViewById<TextView>(R.id.item_student_name)
            textView.text = studentName

        }
    }

    interface StudentItemClickListener {
        fun onStudentItemClickListener(view: View, pos: Int)
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Student?)
    }

    companion object {

        const val IS_USER_ADMIN = "is-user-admin"
        const val DRIVE_NAME = "drive-name"

        @JvmStatic
        fun newInstance(isUserAdmin: Boolean, driveName: String?) =
            StudentsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_USER_ADMIN, isUserAdmin)
                    putString(DRIVE_NAME, driveName)
                }
            }
    }
}
