package com.example.homies.data.source.localstorage

import com.example.homies.data.model.Student

interface LocalStorageRepository {
    fun saveStudent(student: Student)
    fun getStudent(): Student?
    fun clearStudent()
}