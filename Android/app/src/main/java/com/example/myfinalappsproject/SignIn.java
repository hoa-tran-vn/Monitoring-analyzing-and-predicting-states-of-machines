package com.example.myfinalappsproject;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;

public class SignIn extends AppCompatActivity {
    // Variables
    Button signInBtn;
    TextView mHeader,mDesc;
    LinearLayout mCard;
    Animation mTopAnim,mRightAnim;
    TextInputLayout mUserID,mUserPassword;
    TextInputEditText mUserID_edit,mUserPassword_edit;
    ODataConsumer consumer;
    UserObject tempUserObject;
    SharedPreferences sharedPreferences,UserIDandPassWord,mSharedPreferences;
    CheckBox RememberMe_CheckBox;
    LazyLoader lazyLoader;
    String TAG = SignIn.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        sharedPreferences = getSharedPreferences("DataFromUserSignIn",MODE_PRIVATE);
        UserIDandPassWord = getSharedPreferences("UserID_PassWord",MODE_PRIVATE);
        mSharedPreferences = getSharedPreferences("MessageCounting",MODE_PRIVATE);
        SharedPreferences.Editor editor1 = mSharedPreferences.edit();
        editor1.clear();
        editor1.commit();
        editor1.apply();

        Hook();
        AnimationDisPlay();

        mUserID_edit.setText(UserIDandPassWord.getString("User_Name",""));
        mUserPassword_edit.setText(UserIDandPassWord.getString("User_PassWord",""));
        RememberMe_CheckBox.setChecked(UserIDandPassWord.getBoolean("IsCheck",false));

        String serviceUrl = ServiceAddress.serviceURL;
        OClientBehavior bacsicAthur = new OClientBehavior() {
            @Override
            public ODataClientRequest transform(ODataClientRequest request)
            {
                //return request.header("Accept", "application/json");
                return request.header("MaxDataServiceVersion", ODataVersion.V2.asString);
            }
        };
        consumer = ODataConsumers.newBuilder(serviceUrl).setClientBehaviors(bacsicAthur).build();
        // SetUp Connection with WebService

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RememberPassWordAndID(mUserID.getEditText().getText().toString(),mUserPassword.getEditText().getText().toString());
                new CheckIDTask().execute(mUserID.getEditText().getText().toString(),mUserPassword.getEditText().getText().toString());
            }
        });
        // SignIn Button Click function
    } // Function create all acitvity view when we start activity

    private void RememberPassWordAndID(String UserID,String UserPassWord) {
        if (RememberMe_CheckBox.isChecked()){
            SharedPreferences.Editor editor_one = UserIDandPassWord.edit();
            editor_one.putString("User_Name",UserID);
            editor_one.putString("User_PassWord",UserPassWord);
            editor_one.putBoolean("IsCheck",true);
            editor_one.commit();
        } else {
            SharedPreferences.Editor editor_one = UserIDandPassWord.edit();
            editor_one.remove("User_Name");
            editor_one.remove("User_PassWord");
            editor_one.remove("IsCheck");
            editor_one.commit();
        }

    }

    private void AnimationDisPlay() {
        mTopAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_anim);
        mRightAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.right_anim);
        mHeader.setAnimation(mTopAnim);
        mDesc.setAnimation(mTopAnim);
        mCard.setAnimation(mRightAnim);
    } //Function display animations

    private void Hook() {
        mUserPassword = findViewById(R.id.SignIn_UserPassWord);
        mUserID = findViewById(R.id.SignIn_UserID);
        mUserID_edit = findViewById(R.id.SignIn_UserID_Edit);
        mUserPassword_edit =findViewById(R.id.SignIn_UserPassWord_Edit);
        mHeader = findViewById(R.id.SignIn_Header);
        mDesc =findViewById(R.id.SignIn_Desc);
        mCard = findViewById(R.id.SignIn_Card);
        signInBtn = findViewById(R.id.SignIn_Btn);
        RememberMe_CheckBox = findViewById(R.id.SignIn_RememberMe_Btn);
        lazyLoader = findViewById(R.id.loader);
    } // Function hook View object with their ID

    private class CheckIDTask extends AsyncTask<String,Long,Integer>{

       @Override
       protected void onPreExecute() {
           mUserID.setErrorEnabled(false);
           lazyLoader.setVisibility(View.VISIBLE);
           super.onPreExecute();
       }

       @Override
       protected Integer doInBackground(String...params) {

         try{
               String UserID = params[0];
               String UserPassWord = params[1];
             for (OEntity DataFromDataBase : consumer.getEntities("NhanViens").filter("substring(MaSo, 4) eq '" + UserID + "' and MatKhau eq '" + UserPassWord + "' and NghiViec ne true").execute())
             {
                 tempUserObject = new UserObject();
                 tempUserObject.setID(DataFromDataBase.getProperty("Id", Integer.class).getValue());
                 tempUserObject.setIdentityName(DataFromDataBase.getProperty("MaSo", String.class).getValue());
                 tempUserObject.setPassWord(DataFromDataBase.getProperty("MatKhau", String.class).getValue());
                 tempUserObject.setUserName(DataFromDataBase.getProperty("HoTen", String.class).getValue());
                 tempUserObject.setEmail(DataFromDataBase.getProperty("Email",String.class).getValue());
                 return 1;
             }
             if(tempUserObject == null);
             return 0;
         }catch (Exception ex){
             return -1;
         }
       }

       @Override
       protected void onPostExecute(Integer integer) {

           switch (integer){
               case 1:{
                   lazyLoader.setVisibility(View.INVISIBLE);
                   SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.putString("UserPassWord", tempUserObject.getPassWord());
                   editor.putString("UserIndentifyName", tempUserObject.getIdentityName());
                   editor.putString("UserEmail", tempUserObject.getEmail());
                   editor.putString("UserName", tempUserObject.getUserName());
                   editor.apply();
                   // Store data in SharedPreferences name"DataFromUserSignIn" and pass to UserProflie Acivity
//                   startActivity( new Intent(getApplicationContext(),GanttChartActivity.class));
                   startActivity( new Intent(getApplicationContext(),DashBoard.class));
                   break;
               }
               case 0:{
                   lazyLoader.setVisibility(View.INVISIBLE);
                   mUserID.setErrorEnabled(true);
                   mUserID.setError("Sai tài khoản/Tên đăng nhập");
                   break;
               }
               case -1:{
                   lazyLoader.setVisibility(View.INVISIBLE);
                   Toast.makeText(getApplicationContext(),"Kiểm tra kết nối!",Toast.LENGTH_LONG).show();
                   break;
               }
           }
       }
   } // Check User Accounts

}
