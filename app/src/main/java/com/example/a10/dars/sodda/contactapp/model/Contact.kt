package com.example.a10.dars.sodda.contactdb.model

import java.io.Serializable

class Contact : Serializable {
    var id: Int? = null
    var name: String? = null
    var number: String? = null

    constructor()
    constructor(name: String?, number: String?) {
        this.name = name
        this.number = number
    }

    constructor(id: Int?, name: String?, number: String?) {
        this.id = id
        this.name = name
        this.number = number
    }

}