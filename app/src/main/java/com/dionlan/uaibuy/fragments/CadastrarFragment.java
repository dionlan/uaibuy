package com.dionlan.uaibuy.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.dionlan.uaibuy.DispatchActivity;
import com.dionlan.uaibuy.R;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CadastrarFragment extends Fragment {

    private EditText nomePFisicaView;
    private EditText razaoSocialPJView;
    private EditText nomeFantasiaPJuridicaView;
    private EditText usuarioView;
    private EditText senhaView;
    private EditText confirmaSenhaView;
    private EditText emailView;
    private EditText cnpjView;
    private EditText enderecoView;
    private EditText telefoneView;
    private EditText cepView;
    private Button botaoLoginFaceView;
    private TextView textoNomeUsuarioFaceView;
    boolean fotoCamera;
    private Bitmap bitmap;
    private ImageView imagemContatoView;
    private Uri imagemUri = Uri.parse("android.resource://com.parse.starter/drawable/ic_atualiza_foto_perfil.png");

    private Switch mySwitch;
    View view = null;
    private TextView mTextDetails = null;

    private ProfileTracker mProfileTracker = null;
    private AccessTokenTracker mTokenTracker = null;
    private CallbackManager mCallbackManager = null;

    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d("AppInfo", "onSuccess");
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            mTextDetails.setText(constructWelcomeMessage(profile));
        }

        @Override
        public void onCancel() {
            Log.d("AppInfo", "onCancel");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("AppInfo", "onError " + error);
        }
    };

    public CadastrarFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();

        view = inflater.inflate(R.layout.fragment_cadastrar, container, false);
        // Set up the signup form.
        imagemContatoView = (ImageView) view.findViewById(R.id.imagemContatoCadastro);
        nomePFisicaView = (EditText) view.findViewById(R.id.campoNome);
        nomeFantasiaPJuridicaView = (EditText) view.findViewById(R.id.campoNomeFantasia);
        cnpjView = (EditText) view.findViewById(R.id.campoCnpj);
        usuarioView = (EditText) view.findViewById(R.id.campoUsuario);
        senhaView = (EditText) view.findViewById(R.id.campoSenha);
        confirmaSenhaView = (EditText) view.findViewById(R.id.campoConfirmaSenha);
        emailView = (EditText) view.findViewById(R.id.campoEmail);
        enderecoView = (EditText) view.findViewById(R.id.campoEndereco);
        telefoneView = (EditText) view.findViewById(R.id.campoTelefone);
        cepView = (EditText) view.findViewById(R.id.campoCep);
        botaoLoginFaceView = (Button) view.findViewById(R.id.login_button_facebook);
        textoNomeUsuarioFaceView = (TextView) view.findViewById(R.id.text_details_nome_face);

        mySwitch = (Switch) view.findViewById(R.id.mySwitch);

        //set the switch to ON
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mySwitch.setChecked(false);

            nomeFantasiaPJuridicaView.setVisibility(View.GONE);
            cnpjView.setVisibility(View.GONE);
            enderecoView.setVisibility(View.GONE);
            cepView.setVisibility(View.GONE);

        }
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {

                    botaoLoginFaceView = (Button) view.findViewById(R.id.login_button_facebook);
                    botaoLoginFaceView.setVisibility(View.VISIBLE);

                    textoNomeUsuarioFaceView = (TextView) view.findViewById(R.id.text_details_nome_face);
                    textoNomeUsuarioFaceView.setVisibility(View.VISIBLE);

                    razaoSocialPJView.setVisibility(View.GONE);
                    nomeFantasiaPJuridicaView.setVisibility(View.GONE);

                    nomePFisicaView = (EditText) view.findViewById(R.id.campoNome);
                    nomePFisicaView.setVisibility(View.VISIBLE);
                    nomePFisicaView.setHint("Nome");

                    cnpjView.setVisibility(View.GONE);

                    cepView.setVisibility(View.GONE);

                    enderecoView.setVisibility(View.GONE);


                } else {
                    Log.i("AppInfo", "SWITCH PRESSINADO: " + mySwitch.isChecked());
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Comércio selecionado, preencha os campos.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 10, 10);
                    toast.show();

                    //Toast.makeText(getApplicationContext(), "Comércio selecionado, preencha os campos.", Toast.LENGTH_LONG).show();

                    botaoLoginFaceView.setVisibility(View.GONE);
                    textoNomeUsuarioFaceView.setVisibility(View.GONE);

                    nomePFisicaView.setVisibility(View.GONE);

                    razaoSocialPJView = (EditText) view.findViewById(R.id.campoNome);
                    razaoSocialPJView.setVisibility(View.VISIBLE);
                    razaoSocialPJView.setHint("Razão Social");

                    nomeFantasiaPJuridicaView.setVisibility(View.VISIBLE);
                    nomeFantasiaPJuridicaView = (EditText) view.findViewById(R.id.campoNomeFantasia);
                    nomeFantasiaPJuridicaView.setHint("Nome Fantasia");

                    cnpjView = (EditText) view.findViewById(R.id.campoCnpj);
                    cnpjView.setVisibility(View.VISIBLE);
                    cnpjView.setHint("CNPJ");

                    cepView = (EditText) view.findViewById(R.id.campoCep);
                    cepView.setVisibility(View.VISIBLE);
                    cepView.setHint("CEP");

                    enderecoView.setVisibility(View.VISIBLE);
                }
            }
        });

        // Set up the submit button click handler
        view.findViewById(R.id.action_button_signup).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Validate the sign up data
                boolean validationError = false;
                StringBuilder validationErrorMessage = new StringBuilder(getResources().getString(R.string.error_intro));
                if (!mySwitch.isChecked()) {
                    if (isEmpty(nomePFisicaView)) {
                        validationError = true;
                        validationErrorMessage.append("Informe seu nome");
                    }
                } else {
                    if (isEmpty(razaoSocialPJView)) {
                        validationError = true;
                        validationErrorMessage.append("Informe o nome do representante da empresa");
                    }
                    if (isEmpty(nomeFantasiaPJuridicaView)) {
                        validationError = true;
                        validationErrorMessage.append("Informe o nome fantasia do comércio");
                    }
                    if (isEmpty(cnpjView)) {
                        validationError = true;
                        validationErrorMessage.append("Informe seu CNPJ");
                    }
                }
                if (isEmpty(usuarioView)) {
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
                }
                if (isEmpty(senhaView)) {
                    if (validationError) {
                        validationErrorMessage.append(getResources().getString(R.string.error_join));
                    }
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
                }
                if (isEmpty(emailView)) {
                    validationError = true;
                    validationErrorMessage.append("Informe seu e-mail");
                }
                if (isEmpty(cnpjView)) {
                    validationError = true;
                    validationErrorMessage.append("Informe seu CNPJ");
                }
                if (isEmpty(enderecoView)) {
                    validationError = true;
                    validationErrorMessage.append("Informe seu endereço");
                }
                if (isEmpty(telefoneView)) {
                    validationError = true;
                    validationErrorMessage.append("Informe seu telefone");
                }
                if (!isMatching(senhaView, confirmaSenhaView)) {
                    if (validationError) {
                        validationErrorMessage.append(getResources().getString(R.string.error_join));
                    }
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(
                            R.string.error_mismatched_passwords));
                }
                validationErrorMessage.append(getResources().getString(R.string.error_end));

                // If there is a validation error, display the error
                if (validationError) {
                    Toast.makeText(getActivity().getApplicationContext(), validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                // Set up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(getActivity().getApplicationContext());
                dlg.setTitle("Por favor, aguarde...");
                dlg.setMessage("Cadastrando... Por favor, aguarde...");
                dlg.show();

                // Set up a new Parse user
                ParseUser user = new ParseUser();
                if (!mySwitch.isChecked()) { //Cadastro de pessoa física
                    user.put("isComercio", false);
                    user.put("nomePessoaFisica", nomePFisicaView.getText().toString());
                } else { //Cadastro de Pessoa Jurídica
                    user.put("isComercio", true);
                    user.put("nomeFantasia", nomeFantasiaPJuridicaView.getText().toString());
                    user.put("razaoSocial", razaoSocialPJView.getText().toString());
                    user.put("cnpj", cnpjView.getText().toString());
                }
                //Cadastro comum entre entre PF e PJ
                user.setUsername(usuarioView.getText().toString());
                user.setPassword(senhaView.getText().toString());
                user.put("email", emailView.getText().toString());
                user.put("endereco", enderecoView.getText().toString());
                user.put("telefone", telefoneView.getText().toString());
                user.put("cep", cepView.getText().toString());

                byte[] braveData = (imagemContatoView.toString()).getBytes();
                ParseFile imgFile = new ParseFile(nomePFisicaView.getText().toString() + "_" + "Usuario.png", braveData);
                imgFile.saveInBackground();
                user.put("foto", imgFile);

                // Call the Parse signup method
                user.signUpInBackground(new SignUpCallback() {

                    @Override
                    public void done(ParseException e) {
                        dlg.dismiss();
                        if (e != null) {
                            // Show the error message
                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            // Start an intent for the dispatch activity
                            Intent intent = new Intent(getActivity().getApplicationContext(), DispatchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

/*        ImageView imagemAddFotoCadastro = (ImageView) view.findViewById(R.id.imagemAddFotoCadastro);
        imagemAddFotoCadastro.setOnClickListener(new View.OnClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                fotoCamera = false;
                Intent intent = new Intent();
                intent.setType("image*//**//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);
            }
        });*/

       /* view.findViewById(R.id.botaoTirarFoto).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                fotoCamera = true;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);

            } });*/

        // Defines the xml file for the fragment
        //View view = inflater.inflate(R.layout.fragment_cadastrar, container, false);
        // Setup handles to view objects here
        // etFoo = (EditText) view.findViewById(R.id.etFoo);
        //Intent intent = new Intent(getActivity(), SignUpActivity.class);
        //startActivity(intent);
        return view;
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isMatching(EditText etText1, EditText etText2) {
        if (etText1.getText().toString().equals(etText2.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Context context = null;
        Activity activity = (Activity) context;

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (fotoCamera) {
            super.onActivityResult(requestCode, resultCode, data);
            InputStream stream = null;
            if (requestCode == 0 && resultCode == -1) {
                try {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }

                    stream = activity.getContentResolver().openInputStream(data.getData());
                    if (stream == null) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                    } else {
                        bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data.getData());
                    }

                    bitmap = BitmapFactory.decodeStream(stream);
                    imagemContatoView.setImageBitmap(resizeImage(this, bitmap, 120, 120));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        //imagemContatoView.setRotation(90);
                    }
                    imagemUri = data.getData();
                    imagemContatoView.setImageURI(data.getData());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (stream != null)
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }

            }
        } else {

            if (resultCode == -1) {
                if (requestCode == 1) {
                    imagemUri = data.getData();
                    imagemContatoView.setImageURI(data.getData());
                }
            }
        }
    }

    private static Bitmap resizeImage(CadastrarFragment context, Bitmap bmpOriginal, float newWidth, float newHeight) {
        Bitmap novoBmp = null;
        int w = bmpOriginal.getWidth();
        int h = bmpOriginal.getHeight();
        float densityFactor = context.getResources().getDisplayMetrics().density;
        float novoW = newWidth * densityFactor;
        float novoH = newHeight * densityFactor;
        //Calcula escala em percentagem do tamanho original para o novo tamanho
        float scalaW = novoW / w;
        float scalaH = novoH / h;
        // Criando uma matrix para manipulação da imagem BitMap
        Matrix matrix = new Matrix();
        // Definindo a proporção da escala para o matrix
        matrix.postScale(scalaW, scalaH);
        //criando o novo BitMap com o novo tamanho
        novoBmp = Bitmap.createBitmap(bmpOriginal, 0, 0, w, h, matrix, true);
        return novoBmp;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        setupTextDetails(view);
        setupLoginButton(view);
    }

    @Override
    public void onResume(){
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        mTextDetails.setText(constructWelcomeMessage(profile));
    }

    @Override
    public void onStop(){
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }

    private void setupTextDetails(View view) {
        mTextDetails = (TextView) view.findViewById(R.id.text_details_nome_face);
    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d("AppInfo", "" + currentAccessToken);
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d("AppInfo", "" + currentProfile);
                mTextDetails.setText(constructWelcomeMessage(currentProfile));
            }
        };
    }

    private void setupLoginButton(View view) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button_facebook);
        mButtonLogin.setFragment(this);
        mButtonLogin.setCompoundDrawables(null, null, null, null);
        mButtonLogin.setReadPermissions("user_friends");
        mButtonLogin.registerCallback(mCallbackManager, mFacebookCallback);
    }

    private String constructWelcomeMessage(Profile profile) {
        StringBuffer stringBuffer = new StringBuffer();
        if (profile != null) {
            stringBuffer.append("Seja bem vindo(a), " + profile.getName());
            Log.i("AppInfo", "Nome do usuario: " +profile.getName());
        }
        return stringBuffer.toString();
    }
}