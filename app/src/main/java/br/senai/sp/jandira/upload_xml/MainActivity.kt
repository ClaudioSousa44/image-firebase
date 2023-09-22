package br.senai.sp.jandira.upload_xml

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import br.senai.sp.jandira.upload_xml.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.contracts.contract

class MainActivity : AppCompatActivity() {

    //Atributos
    //Representação da classe de objetos de views das telas
    private lateinit var binding: ActivityMainBinding

    //representação da classe de manipulação de endereço (LOCAL) de arquivos
    private var imageUri: Uri? = null

    //Referencia para acesso e manipulação do cloud Storage e firestore
    private lateinit var storageRef: StorageReference
    private lateinit var fibaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVars()
        registerClickEvents()

    }

    //inicializacao dos atributos do firebase
    private fun initVars() {
        storageRef = FirebaseStorage.getInstance().reference.child("images")
        fibaseFirestore = FirebaseFirestore.getInstance()
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        imageUri = it
        binding.imageView.setImageURI(it)
    }

    private fun registerClickEvents() {
        binding.imageView.setOnClickListener {
            resultLauncher.launch("*/*")
        }
        binding.uploadBtn.setOnClickListener{
            uploadImage()
        }
    }

    //upload de imagens do firebase
    private fun uploadImage() {

        binding.progressBar.visibility = View.VISIBLE

        storageRef = storageRef.child(System.currentTimeMillis().toString())

        //Upload V1 inicio
//        imageUri?.let {
//            storageRef.putFile(it).addOnCompleteListener {
//
//                    task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(
//                        this, "UPLOAD REALIZADO COM SUCESSO", Toast.LENGTH_LONG
//                    ).show()
//
//                } else {
//                    Toast.makeText(
//                        this, "HOUVE UM ERRO AO REALIZAR O UPLOAD", Toast.LENGTH_LONG
//                    ).show()
//                }
//
//                binding.progressBar.visibility = View.GONE
//
//            }
//        }

        ///// PROCESSO DE UPLOAD - V2 /////
        imageUri?.let {
            storageRef.putFile(it).addOnCompleteListener { task->

                if (task.isSuccessful) {

                    storageRef.downloadUrl.addOnSuccessListener { uri ->

                        val map = HashMap<String, Any>()
                        map["pic"] = uri.toString()
                        Log.e("link", map.toString() )

                        fibaseFirestore.collection("images").add(map).addOnCompleteListener { firestoreTask ->

                            if (firestoreTask.isSuccessful){
                                Toast.makeText(this, "Uploaded realizado com sucesso", Toast.LENGTH_SHORT).show()

                            }else{
                                Toast.makeText(this, "ERRO ao tentar fazer upload", Toast.LENGTH_SHORT).show()

                            }
                            binding.progressBar.visibility = View.GONE
                            binding.imageView.setImageResource(R.drawable.upload)

                        }
                    }

                }else{

                    Toast.makeText(this,  task.exception?.message, Toast.LENGTH_SHORT).show()

                }

                //BARRA DE PROGRESSO DO UPLOAD
                binding.progressBar.visibility = View.GONE

                //TROCA A IMAGEM PARA A IMAGEM PADRÃO
                binding.imageView.setImageResource(R.drawable.upload)


            }
        }
        ///// PROCESSO DE UPLOAD - V2 /////



    }
}