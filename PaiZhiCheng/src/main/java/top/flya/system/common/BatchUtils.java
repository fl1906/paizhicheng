package top.flya.system.common;


import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;
import top.flya.system.domain.vo.PzcArtistVo;
import top.flya.system.domain.vo.PzcOrganizerVo;
import top.flya.system.domain.vo.PzcTagVo;
import top.flya.system.domain.vo.SysOssVo;
import top.flya.system.service.ISysOssService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BatchUtils {
    @Resource
    private ISysOssService iSysOssService;



    // 假设这里有一个方法可以批量查询新的 imageUrl
    public  Map<Long, String> getNewImageUrls(List<String> imageUrls) {
        List<Long> ossIds = imageUrls.stream().map(Long::parseLong).collect(Collectors.toList());
        return iSysOssService.listByIds(ossIds).stream().collect(Collectors.toMap(SysOssVo::getOssId, SysOssVo::getUrl));
    }

    public List<PzcArtistVo> transformToPzcArtistVo(List<PzcArtistVo> artistList) {
        log.info("transform artistList start: {}", artistList);
        // 获取所有旧的 imageUrl
        List<String> oldImageUrls = artistList.stream()
            .map(PzcArtistVo::getImageUrl)
            .collect(Collectors.toList());

        // 批量查询新的 imageUrl
        Map<Long, String> newImageUrls = getNewImageUrls(oldImageUrls);

        // 使用 Stream API 进行处理
        return artistList.stream()
            // 对列表中的每个元素进行处理
            .map(artist -> {
                // 从 Map 中获取新的 imageUrl
                String newImageUrl = newImageUrls.get(Long.parseLong(artist.getImageUrl()));
                // 创建一个新的 PzcArtistVo 对象，使用查询到的新 imageUrl
                return new PzcArtistVo(
                    artist.getArtistId(),
                    artist.getName(),
                    newImageUrl,
                    artist.getDescription()
                );
            })
            // 将处理后的元素收集到一个新的 List 中
            .collect(Collectors.toList());
    }

    public  List<PzcOrganizerVo> transformToPzcOrganizerVo(List<PzcOrganizerVo> organizerList) {
        log.info("transform organizerList start: {}", organizerList);
        // 获取所有旧的 imageUrl
        List<String> oldImageUrls = organizerList.stream()
            .map(PzcOrganizerVo::getLogo)
            .collect(Collectors.toList());

        // 批量查询新的 imageUrl
        Map<Long, String> newImageUrls = getNewImageUrls(oldImageUrls);

        // 使用 Stream API 进行处理
        return organizerList.stream()
            // 对列表中的每个元素进行处理
            .map(organizer -> {
                // 从 Map 中获取新的 imageUrl
                String newImageUrl = newImageUrls.get(Long.parseLong(organizer.getLogo()));
                // 创建一个新的 PzcArtistVo 对象，使用查询到的新 imageUrl
                return new PzcOrganizerVo(
                    organizer.getOrganizerId(),
                    organizer.getPhone(),
                    organizer.getName(),
                    newImageUrl,
                    organizer.getContent(),
                    organizer.getCreateTime(),
                    organizer.getUpdateTime()
                );
            })
            // 将处理后的元素收集到一个新的 List 中
            .collect(Collectors.toList());
    }

    public  List<PzcTagVo> transformToPzcTagVo(List<PzcTagVo> tagList) {
        log.info("transform tagList start: {}", tagList);
        // 获取所有旧的 imageUrl
        List<String> oldImageUrls = tagList.stream()
            .map(PzcTagVo::getImageUrl)
            .collect(Collectors.toList());

        // 批量查询新的 imageUrl
        Map<Long, String> newImageUrls = getNewImageUrls(oldImageUrls);

        // 使用 Stream API 进行处理
        return tagList.stream()
            // 对列表中的每个元素进行处理
            .map(tag -> {
                // 从 Map 中获取新的 imageUrl
                String newImageUrl = newImageUrls.get(Long.parseLong(tag.getImageUrl()));
                // 创建一个新的 PzcArtistVo 对象，使用查询到的新 imageUrl
                return new PzcTagVo(
                    tag.getTagId(),
                    tag.getName(),
                    newImageUrl,
                    tag.getCreateTime(),
                    tag.getUpdateTime()
                );
            })
            // 将处理后的元素收集到一个新的 List 中
            .collect(Collectors.toList());
    }



}
