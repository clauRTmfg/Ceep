package br.com.alura.ceep.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.alura.ceep.database.AppDatabase
import br.com.alura.ceep.databinding.ActivityListaNotasBinding
import br.com.alura.ceep.extensions.vaiPara
import br.com.alura.ceep.repository.NotaRepository
import br.com.alura.ceep.ui.recyclerview.adapter.ListaNotasAdapter
import br.com.alura.ceep.webclient.NotaWebClient
import coil.decode.withInterruptibleSource
import kotlinx.coroutines.*

class ListaNotasActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityListaNotasBinding.inflate(layoutInflater)
    }
    private val adapter by lazy {
        ListaNotasAdapter(this)
    }

    private val repository by lazy {
        NotaRepository(
            AppDatabase.instancia(this).notaDao(),
            NotaWebClient()
        )
    }

    val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configuraFab()
        configuraRecyclerView()
        lifecycleScope.launch {
            launch {
                while (true) {
                    repository.sincroniza()
                    delay(5000)
                }
            }

            while (true) {
//                repeatOnLifecycle é usado para que o Flow não fique rodando
//                        desnecessariamente quando estivermos em outra tela
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    buscaNotas()
                }
                delay(10000)
            }
        }

        //exemploRetrofit()

    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            buscaNotas()
        }
    }


//    private fun exemploRetrofit() {
//                val call: Call<List<NotaResposta>> = RetrofitInicializador().notaservice.buscaTodas()
//                lifecycleScope.launch(IO) {
//                    val resposta: Response<List<NotaResposta>> = call.execute()
//                    resposta.body()?.let {notaRespostas ->
//                        val notas = notaRespostas.map {
//                            it.nota
//                        }
//                        Log.i("ListaNotas", "onCreate: ${notas}")
//                    }
//                }
//                call.enqueue(object : Callback<List<NotaResposta>?> {
//                    override fun onResponse(
//                        call: Call<List<NotaResposta>?>,
//                        resposta: Response<List<NotaResposta>?>
//                    ) {
//                        resposta.body()?.let {notaRespostas ->
//                            val notas = notaRespostas.map {
//                                it.nota
//                            }
//                            Log.i("ListaNotas", "onCreate: ${notas}")
//                        }
//                    }
//
//                    override fun onFailure(call: Call<List<NotaResposta>?>, t: Throwable) {
//                        Log.e("ListaNotas", "onFailure: ", t)
//                    }
//                })
//    }

    private fun configuraFab() {
        binding.activityListaNotasFab.setOnClickListener {
            Intent(this, FormNotaActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun configuraRecyclerView() {
        binding.activityListaNotasRecyclerview.adapter = adapter
        adapter.quandoClicaNoItem = { nota ->
            vaiPara(FormNotaActivity::class.java) {
                putExtra(NOTA_ID, nota.id)
            }
        }
    }

    private suspend fun buscaNotas() {
        repository.buscaTodas()
            .collect { notasEncontradas ->
                binding.activityListaNotasMensagemSemNotas.visibility =
                    if (notasEncontradas.isEmpty()) {
                        binding.activityListaNotasRecyclerview.visibility = GONE
                        VISIBLE
                    } else {
                        binding.activityListaNotasRecyclerview.visibility = VISIBLE
                        adapter.atualiza(notasEncontradas)
                        GONE
                    }
            }
    }
}