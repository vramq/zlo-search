package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import info.xonix.zlo.search.model.*;
import info.xonix.zlo.search.utils.TimeUtils;
import info.xonix.zlo.search.utils.factory.SiteFactory;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 2:24:05
 */
public class ZloSearcher {
    private static final Logger log = Logger.getLogger(ZloSearcher.class);

    public static final int PERIOD_RECREATE_INDEXER = TimeUtils.parseToMilliSeconds(Config.getProp("searcher.period.recreate.indexer"));

    private SiteFactory<DoubleIndexSearcher> doubleIndexSearcherFactory = new SiteFactory<DoubleIndexSearcher>() {
        @Override
        protected DoubleIndexSearcher create(Site site) {
            return new DoubleIndexSearcher(site, getDateSort());
        }
    };

    public static void clean(IndexReader ir) {
        try {
            if (ir != null)
                ir.close();
        } catch (IOException e) {
            log.error("Error while closing index reader: " + e.getClass());
        }
    }

    public SearchResult search(SearchRequest req) {
        return search(
                req.getSite(),
                Message.formQueryString(
                        req.getText(), req.isInTitle(), req.isInBody(),
                        req.getTopicCode(), req.getNick(), req.getHost(),
                        req.getFromDate(), req.getToDate(),
                        req.isInReg(), req.isInHasUrl(), req.isInHasImg()),
                req.isSearchAll());
    }

    public static Sort getDateSort() {
        // sort causes slow first search & lot memory used!
        return Config.SEARCH_PERFORM_SORT
                ? new Sort(new SortField(MessageFields.DATE, SortField.STRING, true))
                : null;
    }

    private SearchResult search(Site site, String queryStr, boolean searchAll) {
        Assert.notNull(site, "site can't be null!");

        if (!Config.USE_DOUBLE_INDEX) {
            throw new RuntimeException("Old!!!");
        } else {
            return searchDoubleIndex(site, queryStr, null, searchAll);
        }
    }

    private SearchResult searchDoubleIndex(Site site, String queryStr, Sort sort, boolean searchAll) {
        if (sort == null)
            sort = getDateSort();

        SearchResult result;
        try {
            Analyzer analyzer = Message.constructAnalyzer();

            QueryParser parser = new QueryParser(MessageFields.BODY, analyzer);
            parser.setDefaultOperator(searchAll ? QueryParser.AND_OPERATOR : QueryParser.OR_OPERATOR);

            Query query = parser.parse(queryStr);

            log.info("query: " + query);

            DoubleIndexSearcher dis = getDoubleIndexSearcher(site);

            result = new SearchResult(site, query, dis, dis.search(query));
        } catch (ParseException e) {
            throw new SearchException(queryStr, e);
        } catch (IOException e) {
            log.error(e);
            throw new SearchException(queryStr, e);
        }
        return result;
    }

    public void optimizeIndex(Site site) {
        DoubleIndexSearcher dis = getDoubleIndexSearcher(site);

        try {
            dis.moveSmallToBig();
            dis.optimize();
        } catch (IOException e) {
            log.error("Error while optimizingIndex", e);
        }
        // VVV --- won't close - as it closes dis for websearch
//        dis.close();
    }

    public void dropIndex(Site site) throws IOException {
        DoubleIndexSearcher dis = getDoubleIndexSearcher(site);
        dis.drop();
        dis.close();
    }

    /**
     * TODO: make private!
     *
     * @param site
     * @return
     */
    public DoubleIndexSearcher getDoubleIndexSearcher(Site site) {
        return doubleIndexSearcherFactory.get(site);
    }
}