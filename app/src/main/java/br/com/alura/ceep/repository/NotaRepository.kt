package br.com.alura.ceep.repository

import br.com.alura.ceep.database.dao.NotaDao
import br.com.alura.ceep.model.Nota
import br.com.alura.ceep.webclient.NotaWebClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NotaRepository(private val dao: NotaDao,
private val webClient: NotaWebClient) {

    fun buscaTodas(): Flow<List<Nota>> {
        return dao.buscaTodas()
    }

    fun buscaPorId(id: String): Flow<Nota> {
        return dao.buscaPorId(id)
    }

    suspend fun sincroniza() {
        val notasDesativadas = dao.buscaDesativadas().first()
        notasDesativadas.forEach {
            remove(it.id)
        }
        val notasNaoSincronizadas = dao.buscaNaoSincronizadas().first()
        notasNaoSincronizadas.forEach {
            salva(it)
        }
        webClient.buscaTodas()?.let { notas ->
            // a idéia aqui é fazer update no Room sem precisar
            // de uma função dao que faça o comando SQL UPDATE
            val notasSincronizadas = notas.map {
                it.copy(sincronizada = true)
            }
            dao.salva(notasSincronizadas)
        }
    }

    suspend fun remove(id: String) {
        dao.desativa(id)
        if(webClient.remove(id)) {
            dao.remove(id)
        }
    }

    suspend fun salva(nota: Nota) {
        dao.salva(nota.copy(sincronizada = false))
        if (webClient.salva(nota)) {
            // a idéia aqui é fazer update no Room sem precisar
            // de uma função dao que faça o comando SQL UPDATE
            dao.salva(nota.copy(sincronizada = true))
        }
    }
}