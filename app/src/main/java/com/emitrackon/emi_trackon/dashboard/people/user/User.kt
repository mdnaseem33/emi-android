package com.emitrackon.emi_trackon.dashboard.people.user

import com.emitrackon.emi_trackon.dashboard.retailerModule.key.model.Bank
import com.emitrackon.emi_trackon.dashboard.retailerModule.key.model.Brand

object User {

    data class UserModel(
        var id: String,
        var imei1: String,
        var imei2: String,
        var created_at: String,
        var sync: String,
        var status: String
    )


    data class UserListResponse(
        val customer: ArrayList<Customer>
    )

    data class ViewUserDetailsResponse(
        val customer: Customer
    )


    data class Customer(
        val aadhar_back_image: String,
        val aadhar_front_image: String,
        val author: Int,
        val bill_no: String,
        val created_at: String,
        val deleted_at: String,
        val device_detail: String,
        val emi_amount: Int,
        val emi_months: Int,
        val firebase_token: String,
        val first_intallment_date: String,
        val key_type: Int?,
        val brand: Brand?,
        val bank: Bank?,
        val id: Int,
        val image: String,
        val imei1: String,
        val imei2: String,
        val is_link: String,
        val last_sync: String,
        val model: String,
        val name: String,
        val phone: String,
        val retailer_id: Int,
        val sign: String,
        val sim1_network: String,
        val sim1_number: String,
        val sim2_network: String,
        val sim2_number: String,
        var status: Int,
        val updated_at: String
    )


    data class QRResponse(
        val qr: String,
        val status: Int
    )

}