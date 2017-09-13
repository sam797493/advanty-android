package games.adventure.com.advanty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class Gamefirst extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamefirst2);


       


        android.widget.Button Butt21 = (android.widget.Button) findViewById(R.id.button5);

        android.widget.Button Butt31 = (android.widget.Button) findViewById(R.id.button7);

        android.widget.Button Butt41 = (android.widget.Button) findViewById(R.id.button12);

        android.widget.Button Butt51 = (android.widget.Button) findViewById(R.id.button11);

        android.widget.Button Butt61 = (android.widget.Button) findViewById(R.id.button10);

        Butt21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent int1 = new Intent(Gamefirst.this, Menua.class);
                startActivity(int1);




            }
        });

        Butt31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent int1 = new Intent(Gamefirst.this, Menub.class);
                startActivity(int1);


            }
        });

        Butt41.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent int1 = new Intent(Gamefirst.this, Menuc.class);
                startActivity(int1);


            }
        });

        Butt51.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent int1 = new Intent(Gamefirst.this, Menud.class);
                startActivity(int1);


            }
        });

        Butt61.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent int1 = new Intent(Gamefirst.this, Gaa.class);
                startActivity(int1);


            }
        });


    }
}