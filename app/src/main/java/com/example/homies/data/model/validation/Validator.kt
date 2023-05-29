package com.example.homies.data.model.validation

interface Validator<T> {
    fun validate(args: T)
}