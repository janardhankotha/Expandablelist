package com.example.g2evolution.expandablelist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {

    JSONParser jsonParser = new JSONParser();
    List<FeedHeader> listDataHeader;
    List<FeedDetail> feedDetail1 = new ArrayList<FeedDetail>();
    HashMap<String, List<FeedDetail>> listDataChild;


    private LinkedHashMap<String, HeaderInfo> myDepartments = new LinkedHashMap<String, HeaderInfo>();
    private ArrayList<HeaderInfo> deptList = new ArrayList<HeaderInfo>();

    private MyListAdapter listAdapter;
    private ExpandableListView myList;
    //our child listener
    private OnChildClickListener myListItemClicked = new OnChildClickListener() {

        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {

            //get the group header
            HeaderInfo headerInfo = deptList.get(groupPosition);
            //get the child info
            DetailInfo detailInfo = headerInfo.getProductList().get(childPosition);
            //display it or do something with it
            Toast.makeText(getBaseContext(), "Clicked on Detail " + headerInfo.getName()
                    + "/" + detailInfo.getName(), Toast.LENGTH_LONG).show();
            return false;
        }

    };
    //our group listener
    private OnGroupClickListener myListGroupClicked = new OnGroupClickListener() {

        public boolean onGroupClick(ExpandableListView parent, View v,
                                    int groupPosition, long id) {

            //get the group header
            HeaderInfo headerInfo = deptList.get(groupPosition);
            //display it or do something with it
            Toast.makeText(getBaseContext(), "Child on Header " + headerInfo.getName(),
                    Toast.LENGTH_LONG).show();

            return false;
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Spinner spinner = (Spinner) findViewById(R.id.department);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dept_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);*/

        //Just add some data to start with
       // loadData();
        new userdata().execute();

        //get reference to the ExpandableListView
        myList = (ExpandableListView) findViewById(R.id.myList);
        //create the adapter by passing your ArrayList data
        listAdapter = new MyListAdapter(MainActivity.this, deptList);
        //attach the adapter to the list
        myList.setAdapter(listAdapter);

        //expand all Groups
        expandAll();

        //add new item to the List
        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);

        //listener for child row click
        myList.setOnChildClickListener(myListItemClicked);
        //listener for group heading click
        myList.setOnGroupClickListener(myListGroupClicked);


    }

    public void onClick(View v) {

        switch (v.getId()) {

            //add entry to the List
            case R.id.add:

                Spinner spinner = (Spinner) findViewById(R.id.department);
                String department = spinner.getSelectedItem().toString();
                EditText editText = (EditText) findViewById(R.id.product);
                String product = editText.getText().toString();
                editText.setText("");

                //add a new item to the list
                int groupPosition = addProduct(department, product);
                //notify the list so that changes can take effect
                listAdapter.notifyDataSetChanged();

                //collapse all groups
                collapseAll();
                //expand the group where item was just added
                myList.expandGroup(groupPosition);
                //set the current group to be selected so that it becomes visible
                myList.setSelectedGroup(groupPosition);

                break;

            // More buttons go here (if any) ...

        }
    }

    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            myList.expandGroup(i);
        }
    }

    //method to collapse all groups
    private void collapseAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            myList.collapseGroup(i);
        }
    }

    //load some initial data into out list
    /*private void loadData() {

        addProduct("Apparel", "Activewear");
        addProduct("Apparel", "Jackets");
        addProduct("Apparel", "Shorts");

        addProduct("Beauty", "Fragrances");
        addProduct("Beauty", "Makeup");

    }*/

    //here we maintain our products in various departments
    private int addProduct(String department, String product) {

        int groupPosition = 0;

        //check the hash map if the group already exists
        HeaderInfo headerInfo = myDepartments.get(department);
        //add the group if doesn't exists
        if (headerInfo == null) {
            headerInfo = new HeaderInfo();
            headerInfo.setName(department);
            myDepartments.put(department, headerInfo);
            deptList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<DetailInfo> productList = headerInfo.getProductList();
        //size of the children list
        int listSize = productList.size();
        //add to the counter
        listSize++;

        //create a new child and add that to the group
        DetailInfo detailInfo = new DetailInfo();
        detailInfo.setSequence(String.valueOf(listSize));
        detailInfo.setName(product);
        productList.add(detailInfo);
        headerInfo.setProductList(productList);

        //find the group position inside the list
        groupPosition = deptList.indexOf(headerInfo);
        return groupPosition;
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }*/





    public class userdata extends AsyncTask<String, String, String> {
        String responce;
        String message;
        String headers;
        String childs;

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();*/

        }

        protected String doInBackground(String... args) {
            Integer result = 0;
            List<NameValuePair> userpramas = new ArrayList<NameValuePair>();

            Log.e("testing", "jsonParser startedkljhk");
            //userpramas.add(new BasicNameValuePair("feader_reg_id", id));
            //  Log.e("testing", "feader_reg_id" + id);

            JSONObject json = jsonParser.makeHttpRequest("http://www.ahilgroup.com/app/menu.php", "POST", userpramas);

            Log.e("testing", "jsonParser" + json);


            if (json == null) {

                Log.e("testing", "jon11111111111111111");
                // Toast.makeText(getActivity(),"Data is not Found",Toast.LENGTH_LONG);

                return responce;
            } else {
                Log.e("testing", "jon2222222222222");
                // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    JSONObject response = new JSONObject(json.toString());

                    Log.e("testing", "jsonParser2222" + json);

                    //JSONObject jsonArray1 = new JSONObject(json.toString());
                    // Result = response.getString("status");
                    JSONArray posts = response.optJSONArray("categories");
                    Log.e("testing", "jsonParser3333" + posts);



              /* if (posts.equals(null)){
                   listDataHeader = new ArrayList<FeedHeader>();
                   listDataChild= new HashMap<String, FeedDetail>();
               }else{
                   listDataHeader.clear();
                   listDataChild.clear();
               }*/
            /*Initialize array if null*/
                  /*  if (null == listDataHeader || null == listDataChild) {
                        listDataHeader = new ArrayList<FeedHeader>();
                        // listDataChild = new ArrayList<FeedDetail>();
                        listDataChild = new HashMap<String, List<FeedDetail>>();
                    } else {
                        listDataHeader.clear();
                        listDataChild.clear();
                    }*/

                    if (posts == null) {
                        Log.e("testing", "jon11111111111111111");

                        //Toast.makeText(getContext(),"No data found",Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("testing", "jon122222211");
                        Log.e("testing", "jsonParser4444" + posts);

                        for (int i = 0; i < posts.length(); i++) {
                            Log.e("testing", "" + posts);

                            Log.e("testing", "" + i);
                            JSONObject post = posts.optJSONObject(i);
                            // JSONArray posts2 = response.optJSONArray("categories");
                            Log.e("testng", "" + post);
                            headers = post.getString("cat_name");

                            Log.e("testing", "name is 11= " + post.getString("cat_name"));


                            FeedHeader item = new FeedHeader();
                            //     String Rowid = post.getString("category_id");
                            String Title = post.getString("cat_name");
                            //     String date = post.getString("category_id");
                            //     String desc = post.getString("category_id");
                            //     String time = post.getString("category_id");
                            //   String uploads = post.getString("cat_image");
                           // item.setHeaderName(Title);
                            // item.setRowid(Rowid);
                            //   item.setDescription(desc);
                            //  item.setUpload(uploads);
                            //  item.setTime(time);

                           // listDataHeader.add(item);


                            HeaderInfo headerInfo;

                            headerInfo = new HeaderInfo();
                            headerInfo.setName(Title);
                            myDepartments.put(Title, headerInfo);
                            deptList.add(headerInfo);

                            //get the children for the group
                            ArrayList<DetailInfo> productList = headerInfo.getProductList();
                            //size of the children list
                            int listSize = productList.size();
                            //add to the counter
                            listSize++;

                            // FeedDetail feedDetail = new FeedDetail(Title);
                            // listDataHeader.add(item);
                            // listDataChild.put(item.getHeaderName(), feedDetail);

                            JSONArray posts2 = post.optJSONArray("Categort_Details");
                            for (int i1 = 0; i1 < posts2.length(); i1++) {
                                JSONObject post2 = posts2.optJSONObject(i1);
                                FeedDetail item2 = new FeedDetail();
                                //String[] planets = new String[]
                                String Title2 = post2.getString("cat_name");

                                Log.e("testing", "name222 = " + post2.getString("cat_name"));
                                childs = post2.getString("cat_name");

                                /*FeedDetail feedDetail = new FeedDetail(Title2);
                                item2.setTitle(Title2);
                                listDataChild.put(item.getHeaderName(), feedDetail);*/

                                item2.setTitle(Title2);
                                //listDataChild.add(item2);
                                // Log.e("mahi--------------", "mahi--------------------- " + item2.setTitle(Title2));

                                feedDetail1.add(item2);
                                Log.e("mahi--------------", "mahi--------------------- " + feedDetail1.add(item2));

                             //   listDataChild.put(item.getHeaderName(), feedDetail1);
                                ///listDataChild.put(listDataHeader.get(0), wk);*/

                                // listDataChild.add(item2);





                            //create a new child and add that to the group
                                DetailInfo detailInfo = new DetailInfo();
                                detailInfo.setSequence(String.valueOf(listSize));
                                detailInfo.setName(Title2);
                                productList.add(detailInfo);
                                headerInfo.setProductList(productList);

                                //find the group position inside the list
                                //groupPosition = deptList.indexOf(headerInfo);
                            }

                        }



                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return responce;
            }


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            pDialog.dismiss();
            Log.e("testing", "result is === " + result);

           // addProduct(listDataHeader, listDataChild);
           // mEListAdapter.setData(listDataHeader, listDataChild);

            //expListView.setAdapter(mEListAdapter);
            // listviewmyorder.setAdapter(adapter);
            Log.e("testing", "adapter");
        }


    }


}