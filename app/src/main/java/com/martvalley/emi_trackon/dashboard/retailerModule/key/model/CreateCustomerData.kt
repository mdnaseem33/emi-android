package com.martvalley.emi_trackon.dashboard.retailerModule.key.model

import com.martvalley.emi_trackon.dashboard.people.user.User

data class CreateCustomerData(
    val banks: List<Bank>,
    val brands: List<Brand>,
    val loan_id: String,
    val customer: User.Customer?
)