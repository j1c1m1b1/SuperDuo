package barqsoft.footballscores.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQuery;
import android.os.Build;
import android.util.Log;

/**
 * @author Julio Mendoza on 8/27/15.
 */
public class ScoresCursorFactory implements CursorFactory
{
    public ScoresCursorFactory()
    {

    }


    @Override
    public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver,
                            String s, SQLiteQuery sqLiteQuery)
    {
        Log.d("Query: ", "" + sqLiteQuery.toString());
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1)
        {
            return new SQLiteCursor(sqLiteCursorDriver, s, sqLiteQuery);
        }
        else
        {
            return new SQLiteCursor(sqLiteDatabase, sqLiteCursorDriver, s, sqLiteQuery);
        }
    }
}
