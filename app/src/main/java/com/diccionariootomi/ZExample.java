//package com.diccionariootomi;
//
//public class Example {
//     * Copyright (C) 2010 The Android Open Source Project
// *
//         * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
//         *      http://www.apache.org/licenses/LICENSE-2.0
//            *
//            * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
//            * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//            * See the License for the specific language governing permissions and
// * limitations under the License.
//            */
//
//            package com.example.android.searchabledict;
//
//import android.app.SearchManager;
//import android.content.ContentProvider;
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.content.UriMatcher;
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.BaseColumns;
//
//    /**
//     * Provides access to the dictionary database.
//     */
//    public class DictionaryProvider extends ContentProvider {
//        String TAG = "DictionaryProvider";
//
//        public static String AUTHORITY = "com.example.android.searchabledict.DictionaryProvider";
//        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/dictionary");
//
//        // MIME types used for searching words or looking up a single definition
//        public static final String WORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
//                "/vnd.example.android.searchabledict";
//        public static final String DEFINITION_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
//                "/vnd.example.android.searchabledict";
//
//        private DictionaryDatabase mDictionary;
//
//        // UriMatcher stuff
//        private static final int SEARCH_WORDS = 0;
//        private static final int GET_WORD = 1;
//        private static final int SEARCH_SUGGEST = 2;
//        private static final int REFRESH_SHORTCUT = 3;
//        private static final UriMatcher sURIMatcher = buildUriMatcher();
//
//        /**
//         * Builds up a UriMatcher for search suggestion and shortcut refresh queries.
//         */
//        private static UriMatcher buildUriMatcher() {
//            UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
//            // to get definitions...
//            matcher.addURI(AUTHORITY, "dictionary", SEARCH_WORDS);
//            matcher.addURI(AUTHORITY, "dictionary/#", GET_WORD);
//            // to get suggestions...
//            matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
//            matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
//
//            /* The following are unused in this implementation, but if we include
//             * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
//             * could expect to receive refresh queries when a shortcutted suggestion is displayed in
//             * Quick Search Box, in which case, the following Uris would be provided and we
//             * would return a cursor with a single item representing the refreshed suggestion data.
//             */
//            matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
//            matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
//            return matcher;
//        }
//
//        @Override
//        public boolean onCreate() {
//            mDictionary = new DictionaryDatabase(getContext());
//            return true;
//        }
//
//        /**
//         * Handles all the dictionary searches and suggestion queries from the Search Manager.
//         * When requesting a specific word, the uri alone is required.
//         * When searching all of the dictionary for matches, the selectionArgs argument must carry
//         * the search query as the first element.
//         * All other arguments are ignored.
//         */
//        @Override
//        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
//                            String sortOrder) {
//
//            // Use the UriMatcher to see what kind of query we have and format the db query accordingly
//            switch (sURIMatcher.match(uri)) {
//                case SEARCH_SUGGEST:
//                    if (selectionArgs == null) {
//                        throw new IllegalArgumentException(
//                                "selectionArgs must be provided for the Uri: " + uri);
//                    }
//                    return getSuggestions(selectionArgs[0]);
//                case SEARCH_WORDS:
//                    if (selectionArgs == null) {
//                        throw new IllegalArgumentException(
//                                "selectionArgs must be provided for the Uri: " + uri);
//                    }
//                    return search(selectionArgs[0]);
//                case GET_WORD:
//                    return getWord(uri);
//                case REFRESH_SHORTCUT:
//                    return refreshShortcut(uri);
//                default:
//                    throw new IllegalArgumentException("Unknown Uri: " + uri);
//            }
//        }
//
//        private Cursor getSuggestions(String query) {
//            query = query.toLowerCase();
//            String[] columns = new String[] {
//                    BaseColumns._ID,
//                    DictionaryDatabase.KEY_WORD,
//                    DictionaryDatabase.KEY_DEFINITION,
//                    /* SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
//                                     (only if you want to refresh shortcuts) */
//                    SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};
//
//            return mDictionary.getWordMatches(query, columns);
//        }
//
//        private Cursor search(String query) {
//            query = query.toLowerCase();
//            String[] columns = new String[] {
//                    BaseColumns._ID,
//                    DictionaryDatabase.KEY_WORD,
//                    DictionaryDatabase.KEY_DEFINITION};
//
//            return mDictionary.getWordMatches(query, columns);
//        }
//
//        private Cursor getWord(Uri uri) {
//            String rowId = uri.getLastPathSegment();
//            String[] columns = new String[] {
//                    DictionaryDatabase.KEY_WORD,
//                    DictionaryDatabase.KEY_DEFINITION};
//
//            return mDictionary.getWord(rowId, columns);
//        }
//
//        private Cursor refreshShortcut(Uri uri) {
//            /* This won't be called with the current implementation, but if we include
//             * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
//             * could expect to receive refresh queries when a shortcutted suggestion is displayed in
//             * Quick Search Box. In which case, this method will query the table for the specific
//             * word, using the given item Uri and provide all the columns originally provided with the
//             * suggestion query.
//             */
//            String rowId = uri.getLastPathSegment();
//            String[] columns = new String[] {
//                    BaseColumns._ID,
//                    DictionaryDatabase.KEY_WORD,
//                    DictionaryDatabase.KEY_DEFINITION,
//                    SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
//                    SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};
//
//            return mDictionary.getWord(rowId, columns);
//        }
//
//        /**
//         * This method is required in order to query the supported types.
//         * It's also useful in our own query() method to determine the type of Uri received.
//         */
//        @Override
//        public String getType(Uri uri) {
//            switch (sURIMatcher.match(uri)) {
//                case SEARCH_WORDS:
//                    return WORDS_MIME_TYPE;
//                case GET_WORD:
//                    return DEFINITION_MIME_TYPE;
//                case SEARCH_SUGGEST:
//                    return SearchManager.SUGGEST_MIME_TYPE;
//                case REFRESH_SHORTCUT:
//                    return SearchManager.SHORTCUT_MIME_TYPE;
//                default:
//                    throw new IllegalArgumentException("Unknown URL " + uri);
//            }
//        }
//
//        // Other required implementations...
//
//        @Override
//        public Uri insert(Uri uri, ContentValues values) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public int delete(Uri uri, String selection, String[] selectionArgs) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//            throw new UnsupportedOperationException();
//        }
//
//    }
//
//******************************************************************************************************************
//
//    public class SearchableDictionary extends Activity {
//
//        private TextView mTextView;
//        private ListView mListView;
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.main);
//
//            mTextView = (TextView) findViewById(R.id.text);
//            mListView = (ListView) findViewById(R.id.list);
//
//            handleIntent(getIntent());
//        }
//
//        @Override
//        protected void onNewIntent(Intent intent) {
//            // Because this activity has set launchMode="singleTop", the system calls this method
//            // to deliver the intent if this activity is currently the foreground activity when
//            // invoked again (when the user executes a search from this activity, we don't create
//            // a new instance of this activity, so the system delivers the search intent here)
//            handleIntent(intent);
//        }
//
//        private void handleIntent(Intent intent) {
//            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
//                // handles a click on a search suggestion; launches activity to show word
//                Intent wordIntent = new Intent(this, WordActivity.class);
//                wordIntent.setData(intent.getData());
//                startActivity(wordIntent);
//            } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//                // handles a search query
//                String query = intent.getStringExtra(SearchManager.QUERY);
//                showResults(query);
//            }
//        }
//
//        /**
//         * Searches the dictionary and displays results for the given query.
//         * @param query The search query
//         */
//        private void showResults(String query) {
//
//            Cursor cursor = managedQuery(DictionaryProvider.CONTENT_URI, null, null,
//                    new String[] {query}, null);
//
//            if (cursor == null) {
//                // There are no results
//                mTextView.setText(getString(R.string.no_results, new Object[] {query}));
//            } else {
//                // Display the number of results
//                int count = cursor.getCount();
//                String countString = getResources().getQuantityString(R.plurals.search_results,
//                        count, new Object[] {count, query});
//                mTextView.setText(countString);
//
//                // Specify the columns we want to display in the result
//                String[] from = new String[] { DictionaryDatabase.KEY_WORD,
//                        DictionaryDatabase.KEY_DEFINITION };
//
//                // Specify the corresponding layout elements where we want the columns to go
//                int[] to = new int[] { R.id.word,
//                        R.id.definition };
//
//                // Create a simple cursor adapter for the definitions and apply them to the ListView
//                SimpleCursorAdapter words = new SimpleCursorAdapter(this,
//                        R.layout.result, cursor, from, to);
//                mListView.setAdapter(words);
//
//                // Define the on-click listener for the list items
//                mListView.setOnItemClickListener(new OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        // Build the Intent used to open WordActivity with a specific word Uri
//                        Intent wordIntent = new Intent(getApplicationContext(), WordActivity.class);
//                        Uri data = Uri.withAppendedPath(DictionaryProvider.CONTENT_URI,
//                                String.valueOf(id));
//                        wordIntent.setData(data);
//                        startActivity(wordIntent);
//                    }
//                });
//            }
//        }
//
//        @Override
//        public boolean onCreateOptionsMenu(Menu menu) {
//            MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.options_menu, menu);
//
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
//                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//                SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//                searchView.setIconifiedByDefault(false);
//            }
//
//            return true;
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.search:
//                    onSearchRequested();
//                    return true;
//                default:
//                    return false;
//            }
//        }
//
//
//        *******************************************************************************************************************
//
//                * Displays a word and its definition.
//                */
//        public class WordActivity extends Activity {
//            @Override
//            protected void onCreate(Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                setContentView(R.layout.word);
//
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
//                    ActionBar actionBar = getActionBar();
//                    actionBar.setDisplayHomeAsUpEnabled(true);
//                }
//
//                Uri uri = getIntent().getData();
//                Cursor cursor = managedQuery(uri, null, null, null, null);
//
//                if (cursor == null) {
//                    finish();
//                } else {
//                    cursor.moveToFirst();
//
//                    TextView word = (TextView) findViewById(R.id.word);
//                    TextView definition = (TextView) findViewById(R.id.definition);
//
//                    int wIndex = cursor.getColumnIndexOrThrow(DictionaryDatabase.KEY_WORD);
//                    int dIndex = cursor.getColumnIndexOrThrow(DictionaryDatabase.KEY_DEFINITION);
//
//                    word.setText(cursor.getString(wIndex));
//                    definition.setText(cursor.getString(dIndex));
//                }
//            }
//
//            @Override
//            public boolean onCreateOptionsMenu(Menu menu) {
//                MenuInflater inflater = getMenuInflater();
//                inflater.inflate(R.menu.options_menu, menu);
//
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
//                    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//                    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//                    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//                    searchView.setIconifiedByDefault(false);
//                }
//
//                return true;
//            }
//
//            @Override
//            public boolean onOptionsItemSelected(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.search:
//                        onSearchRequested();
//                        return true;
//                    case android.R.id.home:
//                        Intent intent = new Intent(this, SearchableDictionary.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        }
//        *******************************************************************************************************************
//        private static final String TAG = "DictionaryDatabase";
//
//        //The columns we'll include in the dictionary table
//        public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
//        public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;
//
//        private static final String DATABASE_NAME = "dictionary";
//        private static final String FTS_VIRTUAL_TABLE = "FTSdictionary";
//        private static final int DATABASE_VERSION = 2;
//
//        private final DictionaryOpenHelper mDatabaseOpenHelper;
//        private static final HashMap<String,String> mColumnMap = buildColumnMap();
//
//        /**
//         * Constructor
//         * @param context The Context within which to work, used to create the DB
//         */
//        public DictionaryDatabase(Context context) {
//            mDatabaseOpenHelper = new DictionaryOpenHelper(context);
//        }
//
//        /**
//         * Builds a map for all columns that may be requested, which will be given to the
//         * SQLiteQueryBuilder. This is a good way to define aliases for column names, but must include
//         * all columns, even if the value is the key. This allows the ContentProvider to request
//         * columns w/o the need to know real column names and create the alias itself.
//         */
//        private static HashMap<String,String> buildColumnMap() {
//            HashMap<String,String> map = new HashMap<String,String>();
//            map.put(KEY_WORD, KEY_WORD);
//            map.put(KEY_DEFINITION, KEY_DEFINITION);
//            map.put(BaseColumns._ID, "rowid AS " +
//                    BaseColumns._ID);
//            map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
//                    SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
//            map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
//                    SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
//            return map;
//        }
//
//        /**
//         * Returns a Cursor positioned at the word specified by rowId
//         *
//         * @param rowId id of word to retrieve
//         * @param columns The columns to include, if null then all are included
//         * @return Cursor positioned to matching word, or null if not found.
//         */
//        public Cursor getWord(String rowId, String[] columns) {
//            String selection = "rowid = ?";
//            String[] selectionArgs = new String[] {rowId};
//
//            return query(selection, selectionArgs, columns);
//
//            /* This builds a query that looks like:
//             *     SELECT <columns> FROM <table> WHERE rowid = <rowId>
//             */
//        }
//
//        /**
//         * Returns a Cursor over all words that match the given query
//         *
//         * @param query The string to search for
//         * @param columns The columns to include, if null then all are included
//         * @return Cursor over all words that match, or null if none found.
//         */
//        public Cursor getWordMatches(String query, String[] columns) {
//            String selection = KEY_WORD + " MATCH ?";
//            String[] selectionArgs = new String[] {query+"*"};
//
//            return query(selection, selectionArgs, columns);
//
//            /* This builds a query that looks like:
//             *     SELECT <columns> FROM <table> WHERE <KEY_WORD> MATCH 'query*'
//             * which is an FTS3 search for the query text (plus a wildcard) inside the word column.
//             *
//             * - "rowid" is the unique id for all rows but we need this value for the "_id" column in
//             *    order for the Adapters to work, so the columns need to make "_id" an alias for "rowid"
//             * - "rowid" also needs to be used by the SUGGEST_COLUMN_INTENT_DATA alias in order
//             *   for suggestions to carry the proper intent data.
//             *   These aliases are defined in the DictionaryProvider when queries are made.
//             * - This can be revised to also search the definition text with FTS3 by changing
//             *   the selection clause to use FTS_VIRTUAL_TABLE instead of KEY_WORD (to search across
//             *   the entire table, but sorting the relevance could be difficult.
//             */
//        }
//
//        /**
//         * Performs a database query.
//         * @param selection The selection clause
//         * @param selectionArgs Selection arguments for "?" components in the selection
//         * @param columns The columns to return
//         * @return A Cursor over all rows matching the query
//         */
//        private Cursor query(String selection, String[] selectionArgs, String[] columns) {
//            /* The SQLiteBuilder provides a map for all possible columns requested to
//             * actual columns in the database, creating a simple column alias mechanism
//             * by which the ContentProvider does not need to know the real column names
//             */
//            SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
//            builder.setTables(FTS_VIRTUAL_TABLE);
//            builder.setProjectionMap(mColumnMap);
//
//            Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
//                    columns, selection, selectionArgs, null, null, null);
//
//            if (cursor == null) {
//                return null;
//            } else if (!cursor.moveToFirst()) {
//                cursor.close();
//                return null;
//            }
//            return cursor;
//        }
//
//
//        /**
//         * This creates/opens the database.
//         */
//        private static class DictionaryOpenHelper extends SQLiteOpenHelper {
//
//            private final Context mHelperContext;
//            private SQLiteDatabase mDatabase;
//
//            /* Note that FTS3 does not support column constraints and thus, you cannot
//             * declare a primary key. However, "rowid" is automatically used as a unique
//             * identifier, so when making requests, we will use "_id" as an alias for "rowid"
//             */
//            private static final String FTS_TABLE_CREATE =
//                    "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
//                            " USING fts3 (" +
//                            KEY_WORD + ", " +
//                            KEY_DEFINITION + ");";
//
//            DictionaryOpenHelper(Context context) {
//                super(context, DATABASE_NAME, null, DATABASE_VERSION);
//                mHelperContext = context;
//            }
//
//            @Override
//            public void onCreate(SQLiteDatabase db) {
//                mDatabase = db;
//                mDatabase.execSQL(FTS_TABLE_CREATE);
//                loadDictionary();
//            }
//
//            /**
//             * Starts a thread to load the database table with words
//             */
//            private void loadDictionary() {
//                new Thread(new Runnable() {
//                    public void run() {
//                        try {
//                            loadWords();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }).start();
//            }
//
//            private void loadWords() throws IOException {
//                Log.d(TAG, "Loading words...");
//                final Resources resources = mHelperContext.getResources();
//                InputStream inputStream = resources.openRawResource(R.raw.definitions);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                try {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        String[] strings = TextUtils.split(line, "-");
//                        if (strings.length < 2) continue;
//                        long id = addWord(strings[0].trim(), strings[1].trim());
//                        if (id < 0) {
//                            Log.e(TAG, "unable to add word: " + strings[0].trim());
//                        }
//                    }
//                } finally {
//                    reader.close();
//                }
//                Log.d(TAG, "DONE loading words.");
//            }
//
//            /**
//             * Add a word to the dictionary.
//             * @return rowId or -1 if failed
//             */
//            public long addWord(String word, String definition) {
//                ContentValues initialValues = new ContentValues();
//                initialValues.put(KEY_WORD, word);
//                initialValues.put(KEY_DEFINITION, definition);
//
//                return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
//            }
//
//            @Override
//            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//                Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
//                        + newVersion + ", which will destroy all old data");
//                db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
//                onCreate(db);
//            }
//        }
//}
