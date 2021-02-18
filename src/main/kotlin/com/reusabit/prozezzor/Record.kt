/**
Copyright 2021 Reusabit Software LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */


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