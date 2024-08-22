package com.martvalley.emi_trackon.dashboard.settings.controls

object Control {

    data class GetCustomerResponse(
        val action: Action, val customer: Customer
    )

    data class Action(
        val avd: DeviceActionNew,
        val bd: DeviceActionNew,
        val cd: DeviceActionNew,
        val fr: DeviceActionNew,
        val ip: DeviceActionNew,
        val od: DeviceActionNew,
        val sbd: DeviceActionNew,
        val swd: DeviceActionNew,
        val uftd: DeviceActionNew,
        val uip: DeviceActionNew,
        val sms: DeviceActionNew,
        val lock: DeviceActionNew,
        val kgd: DeviceActionNew,
    )

    data class Customer(
        val device_detail: String,
        val firebase_token: String,
        val is_link: String,
        val retailer_id: String,
        val aadhar_back_image: Any,
        val aadhar_front_image: Any,
        val author: Int,
        val bill_no: String,
        val created_at: String,
        val deleted_at: Any,
        val emi_amount: Int,
        val emi_months: Int,
        val first_intallment_date: String,
        val id: Int,
        val image: String?,
        val imei1: String?,
        val imei2: String?,
        val last_sync: String?,
        val model: String?,
        val name: String,
        val phone: String,
        val sign: String,
        val sim1_network: String?,
        val sim1_number: String?,
        val sim2_network: String?,
        val sim2_number: String?,
        val status: Int,
        val updated_at: String,
        val action: Any
    )


    data class DeviceActionOld(
        var sm: String,
        var display: String,
        var value: Boolean,
        var list: String
    )

    data class DeviceActionNew(
        var display: String,
        var value: Boolean,
    )

/////////

    data class EmiTransactionListResponse(
        val record: ArrayList<Record>, val record_count: Int
    )

    data class Record(
        val amount: Int,
        val created_at: String,
        val customer: Customer,
        val customer_id: Int,
        val id: Int,
        val pay_date: String?,
        val pay_on_date: String?,
        val status: String,
        val updated_at: String
    )


    data class SingleEmiTransactionResponse(
        val record: Record
    )


    data class UpdateDateEmiRequest(
        val id: Int, val pay_date: String, val status: String
    )

    data class SurrenderResponse(
        val status: String,
        val code:Int
    )


    data class ActionUpdateRequest(
        val customerId: String, val data: Data
    )

    data class Data(
        val avd: DeviceActionNew,
        val bd: DeviceActionNew,
        val cd: DeviceActionNew,
        val fr: DeviceActionNew,
        val ip: DeviceActionNew,
        val od: DeviceActionNew,
        val sbd: DeviceActionNew,
        val swd: DeviceActionNew,
        val uftd: DeviceActionNew,
        val uip: DeviceActionNew,
        val sms: DeviceActionNew,
        val lock: DeviceActionNew
    )

    data class ActionUpdateResponse(
        val device_update_data: String, val message: String, val status: Int
    )

}