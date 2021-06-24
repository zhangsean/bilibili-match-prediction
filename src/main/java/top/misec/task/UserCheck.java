package top.misec.task;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import top.misec.apiquery.ApiList;
import top.misec.utils.HttpUtil;

import java.util.Objects;

import static top.misec.task.TaskInfoHolder.STATUS_CODE_STR;


/**
 * 登录检查
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 4:57
 */
@Log4j2
public class UserCheck implements Task {

    @Override
    public void run() {
        String requestPram = "";
        JsonObject userJson = HttpUtil.doGet(ApiList.LOGIN + requestPram);

        if (userJson != null) {
            if (userJson.get(STATUS_CODE_STR).getAsInt() == 0
                    && userJson.get("data").getAsJsonObject().get("isLogin").getAsBoolean()) {
                log.info("Cookies有效，登陆成功");

                JsonObject matchInfo = HttpUtil.doGet(ApiList.queryMatchInfo);

                if (matchInfo == null || Objects.isNull(matchInfo.get("data")) || matchInfo.get("data").isJsonNull()) {
                    log.info("获取赛事预测信息失败,您应该从未参与过预测，请手动预测一场比赛后重试");
                } else {
                    matchInfo = matchInfo.get("data").getAsJsonObject().get("guess").getAsJsonObject();
                }
                log.info("获取预测信息成功");
                log.info("预测总场数:{}", matchInfo.get("total_guess").getAsInt());
                log.info("预测胜场数:{}", matchInfo.get("total_success").getAsInt());
                log.info("预测胜率:{}%", matchInfo.get("success_rate").getAsDouble());
                log.info("预测总投入:{}硬币", matchInfo.get("total_stake").getAsInt());
                log.info("预测累计收入:{}硬币", matchInfo.get("total_income").getAsDouble());
            } else {
                log.warn("Cookies可能失效了,请仔细检查Github Secrets中DEDEUSERID SESSDATA BILI_JCT三项的值是否正确、过期");
            }

        } else {
            log.info("用户信息请求失败");
        }

    }

    @Override
    public String getName() {
        String taskName = "登录检查";
        return taskName;
    }
}
