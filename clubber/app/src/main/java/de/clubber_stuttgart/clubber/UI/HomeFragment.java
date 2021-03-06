package de.clubber_stuttgart.clubber.UI;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.clubber_stuttgart.clubber.BusinessLogic.DBConnectionService;
import de.clubber_stuttgart.clubber.BusinessLogic.SelectDate;
import de.clubber_stuttgart.clubber.R;
import de.clubber_stuttgart.clubber.Exception.DateFormattingException;

public class HomeFragment extends Fragment {

    final private String LOG = "HomeFragment";
    public EditText datePicker;
    private Context context;
    private Button filterDateButton;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = getActivity().getApplicationContext();

        //only requests the server automatically when starting the app for the first time with internet connection
        if (MainActivity.initialStartRequestResponseServer) {
            Log.i(LOG,"initial setup of the database. App is being started for the first time");
            Intent serviceIntent = new Intent(context, DBConnectionService.class);
            context.startService(serviceIntent);
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);



        datePicker = view.findViewById(R.id.datePicker);
        //hides the keyboard
        datePicker.setInputType(InputType.TYPE_NULL);
        //start the datePicker Dialog
        new SelectDate(datePicker, getContext());
        Log.d(LOG,"calls SelectDate class and opens Datepickerdialog");

        filterDateButton = (Button) view.findViewById(R.id.eventBtnWithDate);
        filterDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment;
                switch (v.getId()) {
                    //if our button for submitting the date is clicked, this will happen
                    case R.id.eventBtnWithDate:
                        fragment = new EventsFragment();
                        Log.i(LOG,"The datepicker button has been taped...");
                        try {
                            EditText dateInput = getView().findViewById(R.id.datePicker);
                            String date = dateInput.getText().toString();
                            //if the date is empty, the button has been clicked without a chosen date. We print out a toast.
                            //if there is a date, we pass the date through the MainActivity and replace the HomeFragment with the EventFragment
                            if(!(date.equals("Datum auswählen"))){
                                date = SelectDate.formatDate(date);
                                Log.d(LOG,"A date has been picked, replacing HomeFragment with EventsFragment...");
                                MainActivity.setDateInBundle(fragment, date);

                                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
                                Log.d(LOG,"bottom navigation is being set to events...");
                                bottomNav.setSelectedItemId(bottomNav.getMenu().findItem(R.id.nav_events).getItemId());
                                replaceFragment(fragment);
                            }else {
                                Log.d(LOG, "No date has been picked");
                                Toast.makeText(context, "Du musst noch ein Datum auswählen", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }catch (NullPointerException e){
                            Log.w(LOG,e);
                        }
                        catch (DateFormattingException dateFormattingException){
                            dateFormattingException.printStackTrace();
                        }
                }
            }
        });

        return view;
    }

    //if you click on Button after selecting Date Fragment is replaced by filtered Eventlist
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.commit();
    }




}
