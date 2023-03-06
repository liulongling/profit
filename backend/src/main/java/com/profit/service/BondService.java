package com.profit.service;

import com.profit.base.domain.BondInfo;
import com.profit.base.domain.BondInfoExample;
import com.profit.base.mapper.BondInfoMapper;
import com.profit.base.mapper.BondSellLogMapper;
import com.profit.commons.utils.BeanUtils;
import com.profit.commons.utils.DateUtils;
import com.profit.commons.utils.HttpUtil;
import com.profit.dto.BondSellRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class BondService {
    @Resource
    private RestTemplate restTemplate;
    @Value("${gtimg.server-url}")
    private String serverUrl;
    @Resource
    private BondInfoMapper bondInfoMapper;
    @Resource
    private BondSellLogMapper bondSellLogMapper;

    /**
     * 查询指定范围内收益
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public Map<Object, Object> getProfitByDate(String startTime, String endTime) {
        BondSellRequest bondSellRequest = new BondSellRequest();
        bondSellRequest.setStartTime(startTime);
        bondSellRequest.setEndTime(endTime);
        List<Map<Object, Object>> profitList = bondSellLogMapper.listGroupByGpId(bondSellRequest);
        return BeanUtils.list2Map(profitList, "gpId", "income");
    }

    public void refurbish() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = Calendar.DAY_OF_WEEK - 1;
        if (hour >= 9 && hour < 15) {
            List<BondInfo> list = bondInfoMapper.selectByExample(new BondInfoExample());
            for (BondInfo bondInfo : list) {
                Map<String, String> uriMap = new HashMap<>();
                uriMap.put("q", bondInfo.getPlate() + bondInfo.getId());
                ResponseEntity responseEntity = restTemplate.getForEntity
                        (
                                HttpUtil.generateRequestParameters(serverUrl, uriMap),
                                String.class
                        );

                if (responseEntity != null) {
                    String reslut = (String) responseEntity.getBody();
                    String[] str = reslut.split("~");
                    if (str != null) {
                        bondInfo.setPrice(Double.valueOf(str[3]));
                        bondInfoMapper.updateByPrimaryKey(bondInfo);
                    }
                }

            }
        }

    }
}
