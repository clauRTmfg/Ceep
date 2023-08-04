package br.com.alura.ceep.webclient

import br.com.alura.ceep.webclient.services.NotaService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitInicializador {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.15.81:8080/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val notaservice: NotaService = retrofit.create(NotaService::class.java)
}