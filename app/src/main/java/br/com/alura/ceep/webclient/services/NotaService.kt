package br.com.alura.ceep.webclient.services

import br.com.alura.ceep.model.Nota
import br.com.alura.ceep.webclient.model.NotaRequisicao
import br.com.alura.ceep.webclient.model.NotaResposta
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import java.net.IDN

interface NotaService {

    // usando coroutines, não usamos Call
//    @GET("notas")
//    fun buscaTodas(): Call<List<NotaResposta>>

    @GET("notas")
//    suspend fun buscaTodas(): Response<List<NotaResposta>>
    // Podemos usar sem o Response, caso não precisemos de detalhes da
    // msg web. Neste caso não teremos o método body lá na chamada.
    suspend fun buscaTodas(): List<NotaResposta>

    @PUT("notas/{id}")
    suspend fun salva(@Path("id") id: String, @Body nota: NotaRequisicao): Response<NotaResposta>

    @DELETE("notas/{id}")
    suspend fun remove(@Path("id") id: String): Response<Void>

}