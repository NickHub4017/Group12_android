package group12.ucsc.agentmate.ui;

/**
 * Created by NRV on 8/23/2014.
 */




        import android.app.Activity;
        import android.os.Bundle;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import group12.ucsc.agentmate.R;
        import group12.ucsc.agentmate.bll.Representative;
        import group12.ucsc.agentmate.dbc.DatabaseControl;

public class SetPassword extends Activity{
    DatabaseControl dbc=new DatabaseControl(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        final String logged_username;
        final String logged_password;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_password);
        final Representative logged_rep=(Representative)getIntent().getExtras().getSerializable("logged_user");
        logged_username=logged_rep.UserName;
        logged_password=logged_rep.enc_password;
        Button btn_submit=(Button)findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText old_pw_ins_window=(EditText)findViewById(R.id.edit_old_pw);
                EditText new_pw_ins_window=(EditText)findViewById(R.id.edit_new_pwd);
                EditText new_pw_ins_cnf_window=(EditText)findViewById(R.id.edit_new_confirm);
                //String logged_username="p";
                //String logged_password=dbc.password_encoder("pq");

                String old_pw_ins=old_pw_ins_window.getText().toString();
                String new_pw_ins=new_pw_ins_window.getText().toString();
                String new_pw_ins_cnfrm=new_pw_ins_cnf_window.getText().toString();

                String encoded_old_pw=dbc.password_encoder(old_pw_ins);
                if (encoded_old_pw.equals(logged_password)){
                    if (new_pw_ins.equals(new_pw_ins_cnfrm)){
                        //Change password
                        Toast.makeText(SetPassword.this, "Your password change has been completed", Toast.LENGTH_SHORT).show();
                        dbc.change_Password(logged_username, new_pw_ins);
                    }
                    else{
                        Toast.makeText(SetPassword.this, "New password confirmation does not succeeded", Toast.LENGTH_SHORT).show();
                        //Warn about mismatch
                    }
                }
                else{
                    Toast.makeText(SetPassword.this, "Old password does not match", Toast.LENGTH_SHORT).show();
                    //Warn
                }



            }
        });

    }

}

