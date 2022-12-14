package fm.douban.app.control;

import com.alibaba.fastjson.JSON;
import fm.douban.model.*;
import fm.douban.param.SongQueryParam;
import fm.douban.service.FavoriteService;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.FavoriteUtil;
import fm.douban.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainControl {

    private static final Logger LOG = LoggerFactory.getLogger(MainControl.class);

    @Autowired
    SongService songService;

    @Autowired
    SingerService singerService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    FavoriteService favoriteService;

    @GetMapping("/index")
    public String index(Model model){
        setSongData(model);
        setMhzData(model);
        return "index";
    }

    @GetMapping(path = "/search")
    public String search(Model model) {
        return "search";
    }

    @GetMapping(path = "/searchContent")
    @ResponseBody
    public Map searchContent(@RequestParam(name = "keyword") String keyword) {
        SongQueryParam songParam = new SongQueryParam();
        songParam.setName(keyword);
        Page<Song> songs = songService.list(songParam);

        Map result = new HashMap<>();
        result.put("songs", songs);

        return result;
    }

    private void setSongData(Model model) {
        SongQueryParam songParam = new SongQueryParam();
        songParam.setPageNum(1);
        songParam.setPageSize(1);
        Page<Song> songs = songService.list(songParam);

        if (songs != null && !songs.isEmpty()) {
            Song resultSong = songs.getContent().get(0);
            model.addAttribute("song", resultSong);

            List<String> singerIds = resultSong.getSingerIds();

            List<Singer> singers = new ArrayList<>();
            if (singerIds != null && !singerIds.isEmpty()) {
                singerIds.forEach(singerId -> {
                    Singer singer = singerService.get(singerId);
                    singers.add(singer);
                });
            }

            model.addAttribute("singers", singers);
        }
    }

    private void setMhzData(Model model) {
        // ???????????????mhz??????
        List<Subject> subjectDatas = subjectService.getSubjects(SubjectUtil.TYPE_MHZ);

        // ??????????????????????????????????????????
        // ??????????????????????????????????????????????????????????????????
        List<Subject> artistDatas = new ArrayList<>();
        List<Subject> moodDatas = new ArrayList<>();
        List<Subject> ageDatas = new ArrayList<>();
        List<Subject> styleDatas = new ArrayList<>();

        if (subjectDatas != null && !subjectDatas.isEmpty()) {
            subjectDatas.forEach(subject -> {
                if (SubjectUtil.TYPE_SUB_ARTIST.equals(subject.getSubjectSubType())) {
                    artistDatas.add(subject);
                } else if (SubjectUtil.TYPE_SUB_MOOD.equals(subject.getSubjectSubType())) {
                    moodDatas.add(subject);
                } else if (SubjectUtil.TYPE_SUB_AGE.equals(subject.getSubjectSubType())) {
                    ageDatas.add(subject);
                } else if (SubjectUtil.TYPE_SUB_STYLE.equals(subject.getSubjectSubType())) {
                    styleDatas.add(subject);
                } else {
                    // ??????????????????
                    LOG.error("subject data error. unknown subtype. subject=" + JSON.toJSONString(subject));
                }
            });
        }

        // ??????????????????????????????????????????mhz??????????????????????????????????????????
        model.addAttribute("artistDatas", artistDatas);

        // ??????????????????????????????????????????????????????????????????
        List<MhzViewModel> mhzViewModels = new ArrayList<>();
        buildMhzViewModel(moodDatas, "?????? / ??????", mhzViewModels);
        buildMhzViewModel(ageDatas, "?????? / ??????", mhzViewModels);
        buildMhzViewModel(styleDatas, "?????? / ??????", mhzViewModels);
        model.addAttribute("mhzViewModels", mhzViewModels);
    }

    private void buildMhzViewModel(List<Subject> subjects, String title, List<MhzViewModel> mhzViewModels) {
        MhzViewModel mhzVO = new MhzViewModel();
        mhzVO.setSubjects(subjects);
        mhzVO.setTitle(title);
        mhzViewModels.add(mhzVO);
    }

    @GetMapping(path = "/my")
    public String myPage(Model model, HttpServletRequest request, HttpServletResponse response) {
        // ?????? HttpSession ??????
        HttpSession session = request.getSession();
        UserLoginInfo userLoginInfo = (UserLoginInfo)session.getAttribute("userLoginInfo");

        String userId = userLoginInfo.getUserId();

        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setType(FavoriteUtil.TYPE_RED_HEART);
        List<Favorite> favs = favoriteService.list(fav);

        model.addAttribute("favorites", favs);

        List<Song> favedSongs = new ArrayList<>();
        if (favs != null && !favs.isEmpty()) {
            favs.forEach(favorite -> {
                if (FavoriteUtil.TYPE_RED_HEART.equals(favorite.getType()) && FavoriteUtil.ITEM_TYPE_SONG.equals(
                        favorite.getItemType())) {
                    Song song = songService.get(favorite.getItemId());
                    if (song != null) {
                        favedSongs.add(song);
                    }
                }
            });
        }
        model.addAttribute("songs", favedSongs);

        return "my";
    }

    // ?????????????????????????????????????????????????????????????????????
    // ??????????????????????????????????????????????????????
    // ????????????????????????????????????????????????????????????
    @GetMapping(path = "/fav")
    @ResponseBody
    public Map doFav(@RequestParam(name = "itemType") String itemType, @RequestParam(name = "itemId") String itemId,
                     HttpServletRequest request, HttpServletResponse response) {
        Map resultData = new HashMap();
        // ?????? HttpSession ??????
        HttpSession session = request.getSession();
        UserLoginInfo userLoginInfo = (UserLoginInfo)session.getAttribute("userLoginInfo");
        String userId = userLoginInfo.getUserId();

        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setType(FavoriteUtil.TYPE_RED_HEART);
        fav.setItemType(itemType);
        fav.setItemId(itemId);
        List<Favorite> favs = favoriteService.list(fav);
        if (favs == null || favs.isEmpty()) {
            favoriteService.add(fav);
        } else {
            for (Favorite f : favs) {
                favoriteService.delete(f);
            }
        }

        resultData.put("message", "successful");

        return resultData;
    }
}
