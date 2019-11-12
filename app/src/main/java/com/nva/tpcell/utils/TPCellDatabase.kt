package com.nva.tpcell.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nva.tpcell.activities.LoginActivity
import com.nva.tpcell.activities.MainActivity
import com.nva.tpcell.models.Drive
import com.nva.tpcell.models.Student
import java.io.Serializable
import java.util.*

class TPCellDatabase {
    private val db = FirebaseFirestore.getInstance()
    private val dbStudentsRef = db.collection("students")
    private val dbAdminsRef = db.collection("admins")
    private val dbDrivesRef = db.collection("drives")

    var studentData: Student? = null

    private fun unpackStudent(student: Student): HashMap<String, Serializable?> {
        // Converts Student object to HashMap
        return hashMapOf(
            "email" to student.email,
            "name" to student.name,
            "enroll" to student.enroll,
            "phone" to student.phone,
            "aggregate_10th" to student.aggregate_10th,
            "aggregate_12th" to student.aggregate_12th,
            "aggregate_college" to student.aggregate_college
        )
    }

    private fun unpackDrive(drive: Drive): HashMap<String, Serializable?> {
        // Converts Drive object to HashMap
        return hashMapOf(
            "name" to drive.name,
            "desc" to drive.desc,
            "aggregate_10th" to drive.aggregate_10th,
            "aggregate_12th" to drive.aggregate_12th,
            "aggregate_college" to drive.aggregate_college
        )
    }

    fun addStudentObject(context: Context?, student: Student) {
        // Adds Student to the database

        val studentData = unpackStudent(student)

        // Writing document with email as ID and HashMap as data
        dbStudentsRef.document(student.email.toLowerCase(Locale.getDefault()))
            .set(studentData)
            .addOnSuccessListener {
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                // End the fragment
                (context as MainActivity).supportFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            }
    }

    fun addDriveObject(context: Context?, drive: Drive) {
        // Adds Drive to the database
        val driveData = unpackDrive(drive)

        // Writing document with name as ID and HashMap as data
        dbDrivesRef.document(drive.name.toLowerCase(Locale.getDefault()))
            .set(driveData)
            .addOnSuccessListener {
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                // End the fragment
                (context as MainActivity).supportFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            }
    }

    fun deleteStudent(context: Context?, email: String?) {
        // Deletes a Student from the Database
        if ((email != "") && (email != null)) {
            dbStudentsRef.document(email.toLowerCase(Locale.getDefault()))
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                    // End the fragment
                    (context as MainActivity).supportFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                }
        }
    }

    fun deleteDrive(context: Context?, name: String?) {
        // Deletes a Drive from the Database
        if ((name != "") && (name != null)) {
            dbDrivesRef.document(name.toLowerCase(Locale.getDefault()))
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                    // End the fragment
                    (context as MainActivity).supportFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                }
        }
    }

    fun getStudentsList(driveName: String?): Query {
        // Returns Query of all Students if driveName is null, else gets only those eligible for drive
        return if (driveName == null) {
            dbStudentsRef.orderBy("name", Query.Direction.ASCENDING)
        } else {
            dbDrivesRef.document(driveName).collection("eligible")
                .orderBy("name", Query.Direction.ASCENDING)
        }
    }

    fun getDrivesList(): Query {
        // Returns Query for list of all drives
        return dbDrivesRef.orderBy("name", Query.Direction.ASCENDING)
    }

    fun getStudent(context: Context, email: String?) {
        // Get Student from the database and store it in studentData class variable
        if (email != null) {
            dbStudentsRef.document(email)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    studentData = documentSnapshot.toObject(Student::class.java)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Student doesn't exist", Toast.LENGTH_LONG).show()
                }
        }
    }

    fun startLogin(context: Context, email: String?) {
        // Launch main activity with admin or student rights
        if (email != null) {
            dbAdminsRef.document(email.toLowerCase(Locale.getDefault())).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val isUserAdminStr = document.get("is_admin")
                        val isUserAdmin = isUserAdminStr == "y"

                        // if user has attribute is_admin = y, then start activity as Admin

                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("is-user-admin", isUserAdmin)
                        context.startActivity(intent)
                        (context as LoginActivity).finish()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        } else {
            Toast.makeText(context, "Email is null", Toast.LENGTH_LONG).show()
        }

    }
}