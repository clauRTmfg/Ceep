package br.com.alura.ceep.webclient

import android.util.Log
import br.com.alura.ceep.model.Nota
import br.com.alura.ceep.webclient.model.NotaRequisicao
import br.com.alura.ceep.webclient.services.NotaService
import retrofit2.Response

private const val TAG = "NotaWebClient"

class NotaWebClient {

    private val notaService: NotaService = RetrofitInicializador().notaservice

    suspend fun buscaTodas(): List<Nota>? {
        return try {
            val notasResposta = notaService.buscaTodas()
            notasResposta.map {
                it.nota
            }
        } catch (e: Exception) {
            Log.e(TAG, "buscaTodas: ", e)
            null
        }
    }

    suspend fun salva(nota: Nota): Boolean {
        try {
            val resposta = notaService.salva(nota.id, NotaRequisicao(
                titulo = nota.titulo,
                descricao = nota.descricao,
                imagem = nota.imagem
            ))
            return resposta.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "salva: Falha ao tentar salvar ", e)
        }
        return false
    }

    suspend fun remove(id: String): Boolean {
        try {
            notaService.remove(id)
            // aqui não usamos o isSuccessful por conta de um eventual caso em que
            // o id já não exista na web.
            return true
        } catch (e: Exception) {
            Log.e(TAG, "remove: Falha ao tentar remover ", e)
        }
        return false
    }
}