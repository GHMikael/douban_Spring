package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;


@Service
public class SongServiceImpl implements SongService {
    private static final Logger LOG = LoggerFactory.getLogger(SongServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public Song add(Song song ){
        if(song == null){
            LOG.error("input song data is null");
            return null;
        }
        if(song.getGmtCreated() == null){
            song.setGmtCreated(LocalDateTime.now());
        }
        if(song.getGmtModified() == null){
            song.setGmtModified(LocalDateTime.now());
        }
        return mongoTemplate.insert(song);
    }

    @Override
    public Song get(String songId){
        if(!StringUtils.hasText(songId)){
            LOG.error("input songId id blank");
            return null;
        }
        return mongoTemplate.findById(songId,Song.class);
    }

    @Override
    public Page<Song> list(SongQueryParam songParam) {
        // 作为服务，要对入参进行判断，不能假设被调用时，入参一定正确
        if (songParam == null) {
            LOG.error("input song data is not correct.");
            return null;
        }

        // 总条件
        Criteria criteria = new Criteria();
        // 可能有多个子条件
        List<Criteria> subCris = new ArrayList();
        if (StringUtils.hasText(songParam.getName())) {
            subCris.add(Criteria.where("name").is(songParam.getName()));
        }

        if (StringUtils.hasText(songParam.getLyrics())) {
            subCris.add(Criteria.where("lyrics").is(songParam.getLyrics()));
        }

        if (!subCris.isEmpty()) {
            criteria.andOperator(subCris.toArray(new Criteria[]{}));
        }

        // 条件对象构建查询对象
        Query query = new Query(criteria);
        // 总数
        long count = mongoTemplate.count(query, Song.class);
        // 构建分页对象。注意此对象页码号是从 0 开始计数的。
        Pageable pageable = PageRequest.of(songParam.getPageNum() - 1, songParam.getPageSize());
        query.with(pageable);

        // 查询结果
        List<Song> songs = mongoTemplate.find(query, Song.class);
        // 构建分页器
        Page<Song> pageResult = PageableExecutionUtils.getPage(songs, pageable, new LongSupplier() {
            @Override
            public long getAsLong() {
                return count;
            }
        });

        return pageResult;
    }

    @Override
    public boolean modify(Song song){
        if(song == null||!StringUtils.hasText(song.getId())){
            LOG.error("input song data is not correct");
            return false;
        }
        // 主键不能修改，作为查询条件
        Query query = new Query(Criteria.where("id").is(song.getId()));

        Update updateData = new Update();
        // 每次修改都更新 gmtModified 为当前时间
        updateData.set("gmtModified", LocalDateTime.now());
        // 值为 null 表示不修改。值为长度为 0 的字符串 "" 表示清空此字段
        if (song.getName() != null) {
            updateData.set("name", song.getName());
        }

        if (song.getLyrics() != null) {
            updateData.set("lyrics", song.getLyrics());
        }

        if (song.getUrl() != null) {
            updateData.set("url", song.getUrl());
        }
        UpdateResult result = mongoTemplate.updateFirst(query,updateData,Song.class);
        return result != null && result.getModifiedCount() > 0;
    }
    @Override
    public boolean delete(String songId){
        if(!StringUtils.hasText(songId)){
            LOG.error("input songId is blank.");
            return false;
        }
        Song song = new Song();
        song.setId(songId);
        DeleteResult result = mongoTemplate.remove(song);
        return result != null && result.getDeletedCount() > 0;
    }

}
