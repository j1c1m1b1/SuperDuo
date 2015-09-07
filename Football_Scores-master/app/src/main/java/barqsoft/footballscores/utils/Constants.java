package barqsoft.footballscores.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Julio Mendoza on 8/26/15.
 */
public class Constants
{
    //Main Activity
    public static final String CURRENT_PAGER = "Pager_Current";
    public static final String SELECTED_MATCH = "Selected_match";
    public static final String PAGER_FRAGMENT_KEY = "pagerFragment";
    public static final int INVALID_VALUE = -1;
    public static final String ACTION_SERVER_DOWN = "barqsoft.footballscores.ACTION_SERVER_DOWN";

    //Pager Fragment
    public static final String FRAGMENT_DATE = "fragment_date_arg";
    public static final int NUM_PAGES = 5;

    //Request Match Data
    public static final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
    public static final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
    public static final String TOKEN_HEADER = "X-Auth-Token";
    public static final String N2 = "n2";
    public static final String P2 = "p2";

    //JSON Parsing

    //Leagues
    public static final String PREMIER_LEGAUE = "354";
    public static final String CHAMPIONS_LEAGUE = "362";
    public static final String BUNDESLIGA = "351";
    public static final String BUNDESLIGA1 = "394";
    public static final String BUNDESLIGA2 = "395";
    public static final String LIGUE1 = "396";
    public static final String LIGUE2 = "397";
    public static final String PREMIER_LEAGUE = "398";
    public static final String PRIMERA_DIVISION = "399";
    public static final String SEGUNDA_DIVISION = "400";
    public static final String SERIE_A = "401";
    public static final String PRIMERA_LIGA = "402";
    public static final String BUNDESLIGA3 = "403";
    public static final String EREDIVISIE = "404";


    public static final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
    public static final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
    public static final String FIXTURES = "fixtures";
    public static final String LINKS = "_links";
    public static final String SOCCER_SEASON = "soccerseason";
    public static final String SELF = "self";
    public static final String MATCH_DATE = "date";
    public static final String HOME_TEAM = "homeTeamName";
    public static final String AWAY_TEAM = "awayTeamName";
    public static final String RESULT = "result";
    public static final String HOME_GOALS = "goalsHomeTeam";
    public static final String AWAY_GOALS = "goalsAwayTeam";
    public static final String MATCH_DAY = "matchday";
    public static final String HREF = "href";
    public static final String UTC = "UTC";

    //Request Int Def
    public static final int RESULT_CODE_SUCCESS = 0;

    public static final int RESULT_CODE_SERVER_DOWN = 1;

    public static final int RESULT_CODE_INVALID_DATA = 2;

    public static final int RESULT_CODE_NO_DATA = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RESULT_CODE_SUCCESS, RESULT_CODE_SERVER_DOWN, RESULT_CODE_INVALID_DATA,
            RESULT_CODE_NO_DATA})
    public @interface MatchStatus{}

}
