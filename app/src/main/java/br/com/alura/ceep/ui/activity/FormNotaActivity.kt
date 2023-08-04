package br.com.alura.ceep.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.alura.ceep.R
import br.com.alura.ceep.database.AppDatabase
import br.com.alura.ceep.databinding.ActivityFormNotaBinding
import br.com.alura.ceep.extensions.tentaCarregarImagem
import br.com.alura.ceep.model.Nota
import br.com.alura.ceep.repository.NotaRepository
import br.com.alura.ceep.ui.dialog.FormImagemDialog
import br.com.alura.ceep.webclient.NotaWebClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FormNotaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFormNotaBinding.inflate(layoutInflater)
    }

    // pra que este uso de Flow ??
    private var imagem: MutableStateFlow<String?> = MutableStateFlow(null)
    //https://images.pexels.com/photos/2288683/pexels-photo-2288683.jpeg

    private val repository by lazy {
        NotaRepository(
            AppDatabase.instancia(this).notaDao(),
            NotaWebClient()
        )
    }

    private var notaId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.activityFormNotaToolbar)
        tentaCarregarIdDaNota()
        configBotaoImagem()
        lifecycleScope.launch {
            launch {
                tentaBuscarNota()
            }
            launch {
                carregaImagem()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.form_nota_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.form_nota_menu_salvar -> {
                salva()
            }
            R.id.form_nota_menu_remover -> {
                remove()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun tentaCarregarIdDaNota() {
        notaId = intent.getStringExtra(NOTA_ID)
    }

    private suspend fun tentaBuscarNota() {
        notaId?.let { id ->
            repository.buscaPorId(id)
                .filterNotNull()
                .collect { notaEncontrada ->
                    // esta linha... precisa mesmo ??
                    notaId = notaEncontrada.id
                    imagem.value = notaEncontrada.imagem
                    binding.activityFormNotaTitulo.setText(notaEncontrada.titulo)
                    binding.activityFormNotaDescricao.setText(notaEncontrada.descricao)
                }
        }

    }

    private fun configBotaoImagem() {
        binding.activityFormNotaAdicionarImagem.setOnClickListener {
            FormImagemDialog(this)
                .mostra(imagem.value) { imagemCarregada ->
                    binding.activityFormNotaImagem
                        .tentaCarregarImagem(imagemCarregada)
                    imagem.value = imagemCarregada
                }
        }
    }

    private suspend fun carregaImagem() {
        val imagemNota = binding.activityFormNotaImagem
        imagem.collect {
            imagemNota.visibility =
                if (it.isNullOrBlank())
                    GONE
                else {
                    imagemNota.tentaCarregarImagem(it)
                    VISIBLE
                }
        }
    }

    private fun remove() {
        lifecycleScope.launch {
            notaId?.let {
                repository.remove(it)
            }
            finish()
        }
    }

    private fun salva() {
        val nota = criaNota()
        lifecycleScope.launch {
            repository.salva(nota)
            finish()
        }
    }

    private fun criaNota(): Nota {
        val titulo = binding.activityFormNotaTitulo.text.toString()
        val descricao = binding.activityFormNotaDescricao.text.toString()
        return notaId?.let {
            Nota(
                id = it,
                titulo = titulo,
                descricao = descricao,
                imagem = imagem.value
            )
        } ?: Nota(
            titulo = titulo,
            descricao = descricao,
            imagem = imagem.value
        )
    }

}
