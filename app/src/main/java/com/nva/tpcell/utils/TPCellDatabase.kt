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

class TPCellDatabase {
    private val db = FirebaseFirestore.getInstance()
    private val dbStudentsRef = db.collection("students")
    private val dbAdminsRef = db.collection("admins")
    private val dbDrivesRef = db.collection("drives")
    private var isUserAdmin: Boolean = false

    var studentData: Student? = null

    private fun unpackStudent(student: Student): HashMap<String, Serializable?> {
        // Converts object to HashMap
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
        return hashMapOf(
            "name" to drive.name,
            "desc" to drive.desc,
            "aggregate_10th" to drive.aggregate_10th,
            "aggregate_12th" to drive.aggregate_12th,
            "aggregate_college" to drive.aggregate_college
        )
    }

    fun addDriveObject(context: Context?, drive: Drive) {
        val driveData = unpackDrive(drive)
        dbDrivesRef.document(drive.name)
            .set(driveData)
            .addOnSuccessListener {
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            }
    }

    fun addStudentObject(context: Context?, student: Student) {

        // Making HashMap from Student object
        val studentData = unpackStudent(student)

        // Writing document with email as ID and hashmap as data
        dbStudentsRef.document(student.email)
            .set(studentData)
            .addOnSuccessListener {
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            }
    }

    fun deleteStudent(context: Context, email: String) {

        dbStudentsRef.document(email)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            }
    }

    fun checkAdmin(context: Context, email: String?) {

        if (email != null) {
            dbAdminsRef.document(email).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val isUserAdminStr = document.get("is_admin")
                        if (isUserAdminStr == "y") {
                            isUserAdmin = true
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        } else {
            Toast.makeText(context, "Email is null", Toast.LENGTH_LONG).show()
        }
    }

    fun getDrivesList(): Query {
        return dbDrivesRef.orderBy("name", Query.Direction.ASCENDING)
    }

    fun getStudentsList(driveName: String?): Query {

        return if (driveName == null) {
            dbStudentsRef.orderBy("name", Query.Direction.ASCENDING)
        } else {
            dbDrivesRef.document(driveName).collection("eligible")
                .orderBy("name", Query.Direction.ASCENDING)
        }
    }

    fun getStudent(context: Context, email: String?) {

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

        if (email != null) {
            dbAdminsRef.document(email).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val isUserAdminStr = document.get("is_admin")
                        isUserAdmin = isUserAdminStr == "y"

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