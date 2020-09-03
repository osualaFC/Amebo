package com.example.amebo.model

class Users {


    private var uid: String =""
    private var cover: String =""
    private var facebook: String =""
    private var instagram: String =""
    private var profile: String =""
    private var search: String =""
    private var status: String =""
    private var userName: String =""
    private var website: String =""

    constructor()

    constructor(
        uid:String,
        cover:String,
        facebook:String,
        instagram:String,
        profile:String,
        search:String,
        status:String,
        userName:String,
        website:String

    ){
        this.uid = uid
        this.cover = cover
        this.facebook = facebook
        this.instagram = instagram
        this.profile = profile
        this.search = search
        this.status =status
        this.userName = userName
        this.website = website

    }
    /**getters and setters**/

    fun getUID():String?{
        return uid
    }
    fun setUID(uid:String){
        this.uid = uid
    }

    fun getCOVER():String?{
        return cover
    }
    fun setCOVER(cover:String){
        this.cover = cover
    }

    fun getFACEBOOK():String?{
        return facebook
    }
    fun setFACEBOOK(facebook:String){
        this.facebook = facebook
    }

    fun getINSTAGRAM():String?{
        return instagram
    }
    fun setINSTAGRAM(instagram:String){
        this.instagram= instagram
    }

    fun getPROFILE():String?{
        return profile
    }
    fun setPROFILE(profile:String){
        this.profile = profile
    }

    fun getSEARCH():String?{
        return search
    }
    fun setSEARCH(search:String){
        this.search = search
    }

    fun getSTATUS():String?{
        return status
    }
    fun setSTATUS(status:String){
        this.status = status
    }

    fun getUSERNAME():String?{
        return userName
    }
    fun setUSERNAME(userName:String){
        this.userName = userName
    }

    fun getWEBSITE():String?{
        return website
    }
    fun setWEBSITE(website:String){
        this.website = website
    }



}