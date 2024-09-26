package com.emitrackon.emi_trackon.dashboard.retailerModule.key.model

data class CreateCustomerData(
    val banks: List<Bank>,
    val brands: List<Brand>,
    val loan_id: String
)