package com.reusabit.prozezzor

data class Record(
  val name: String? = null,
  val companyName: String? = null,
  val primaryPhoneNumber: String? = null,
  //val cellPhoneNumber: String? = null,
  //val officePhoneNumber: String? = null,
  //val homePhoneNumber: String? = null,
  //val otherPhoneNumbers: List<String> = ArrayList(),
  val website: String? = null,
  val linkedin: String? = null,
  val primaryEmail: String? = null,
  //val otherEmails: List<String> = ArrayList(),
  //val description: String? = null
) {
  data class Builder(
    var name: String? = null,
    var companyName: String? = null,
    var primaryPhoneNumber: String? = null,
    //var cellPhoneNumber: String? = null,
    //var officePhoneNumber: String? = null,
    //var homePhoneNumber: String? = null,
    //var otherPhoneNumbers: ArrayList<String> = ArrayList(),
    var website: String? = null,
    var linkedin: String? = null,
    var primaryEmail: String? = null,
    //var otherEmails: ArrayList<String> = ArrayList(),
    var description: String? = null
  ) {
    fun build() = Record(
      name = name,
      companyName = companyName,
      primaryPhoneNumber = primaryPhoneNumber,
      //cellPhoneNumber = cellPhoneNumber,
      //officePhoneNumber = officePhoneNumber,
      //homePhoneNumber = homePhoneNumber,
      //otherPhoneNumbers = ArrayList<String>().apply { addAll(otherPhoneNumbers) },
      website = website,
      linkedin = linkedin,
      primaryEmail = primaryEmail,
      //otherEmails = ArrayList<String>().apply { addAll(otherEmails) },
    )
  }
}