package com.example.avaliacao_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.avaliacao_app.ui.theme.AvaliacaoAppTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*


data class Produto(
    val nome: String,
    val categoria: String,
    val preco: Double,
    val quantidade: Int)


class Estoque {
    companion object {
        val produtos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto) {
            produtos.add(produto)
        }

        fun calcularValorTotalEstoque(): Double {
            return produtos.sumOf { it.preco * it.quantidade }
        }
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LayoutMain()
        }
    }
}


@Composable
fun LayoutMain() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "cadastroProduto") {
        composable("cadastroProduto") { TelaCadastroProduto(navController) }
        composable("listaProdutos") { TelaListaProdutos(navController) }
        composable("detalhesProduto/{nome}") { backStackEntry ->
            val nome = backStackEntry.arguments?.getString("nome")
            val produto = Estoque.produtos.find { it.nome == nome }
            if (produto != null) {
                TelaDetalhesProduto(navController, produto)
            }
        }
        composable("estatisticas") { TelaEstatisticas() }
    }
}

@Composable
fun TelaCadastroProduto(navController: NavController) {
    val context = LocalContext.current
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = nome,
            onValueChange = { newText: String ->
                nome = newText
            },
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = categoria,
            onValueChange = { newText: String ->
                categoria = newText
            },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = preco,
            onValueChange = { newText: String ->
                preco = newText
            },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = quantidade,
            onValueChange = { newText: String ->
                quantidade = newText
            },
            label = { Text("Quantidade") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                if (nome.isNotBlank() && categoria.isNotBlank() && preco.isNotBlank() && quantidade.isNotBlank()) {
                    val precoDouble = preco.toDoubleOrNull()
                    val quantidadeInt = quantidade.toIntOrNull()

                    if (precoDouble != null && quantidadeInt != null && precoDouble >= 0 && quantidadeInt > 0) {
                        Estoque.adicionarProduto(Produto(nome, categoria, precoDouble, quantidadeInt))
                        navController.navigate("listaProdutos")
                    } else {
                        Toast.makeText(context, "Preço deve ser >= 0 e quantidade > 0", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        ) {
            Text("Cadastrar Produto")
        }
    }
}


@Composable
fun TelaListaProdutos(navController: NavController) {
    val produtos = Estoque.produtos

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(produtos) { produto ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${produto.nome} (${produto.quantidade} unidades)")
                Button(onClick = {
                    navController.navigate("detalhesProduto/${produto.nome}")
                }) {
                    Text("Detalhes")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    // volta para a tela de cadastro
                    navController.popBackStack()
                }) {
                    Text("Voltar para Cadastro")
                }
            }
        }
    }
}


@Composable
fun TelaDetalhesProduto(navController: NavController, produto: Produto) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Nome: ${produto.nome}")
        Text("Categoria: ${produto.categoria}")
        Text("Preço: R$ ${produto.preco}")
        Text("Quantidade em estoque: ${produto.quantidade}")

        Button(onClick = { navController.navigateUp() }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Voltar")
        }
    }
}


@Composable
fun TelaEstatisticas() {
    val valorTotal = Estoque.calcularValorTotalEstoque()
    val quantidadeTotal = Estoque.produtos.sumOf { it.quantidade }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Valor Total do Estoque: R$ $valorTotal")
        Text("Quantidade Total de Produtos: $quantidadeTotal")
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLayoutMain() {
    LayoutMain()
}