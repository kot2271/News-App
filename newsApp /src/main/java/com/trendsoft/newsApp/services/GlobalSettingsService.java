package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.dto.responseDto.GlobalSettingsDto;
import com.trendsoft.newsApp.models.Enums.GlobalSetting;
import com.trendsoft.newsApp.models.GlobalSettings;
import com.trendsoft.newsApp.repositories.GlobalSettingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GlobalSettingsService {
  private GlobalSettingsRepository globalSettingsRepository;

  /** Получение настроек блога */
  public GlobalSettingsDto getGlobalSettings() {
    List<GlobalSettings> globalSettingsList = globalSettingsRepository.findAll();
    GlobalSettingsDto globalSettingsDto = new GlobalSettingsDto();
    globalSettingsList.forEach(
        globalSettings -> {
          if (globalSettings.getCode().equals(GlobalSetting.MULTIUSER_MODE)) {
            globalSettingsDto.setMultiUserMode(globalSettings.getValue());
          }
          if (globalSettings.getCode().equals(GlobalSetting.POST_PREMODERATION)) {
            globalSettingsDto.setPostPreModeration(globalSettings.getValue());
          }
          if (globalSettings.getCode().equals(GlobalSetting.STATISTICS_IS_PUBLIC)) {
            globalSettingsDto.setStatisticIsPublic(globalSettings.getValue());
          }
        });
    return globalSettingsDto;
  }

  /** сохранение новых настроек блога */
  public void addNewSettings(GlobalSettingsDto globalSettingsDto) {

    globalSettingsRepository.updateValue(
        globalSettingsDto.isMultiUserMode(), String.valueOf(GlobalSetting.MULTIUSER_MODE));
    globalSettingsRepository.updateValue(
        globalSettingsDto.isPostPreModeration(), String.valueOf(GlobalSetting.POST_PREMODERATION));
    globalSettingsRepository.updateValue(
        globalSettingsDto.isStatisticIsPublic(),
        String.valueOf(GlobalSetting.STATISTICS_IS_PUBLIC));
  }

  /** Сохранение изначальных настроек блога */
  @PostConstruct
  private void postConstruct() {
    List<GlobalSettings> globalSettingsList = new ArrayList<>();
    GlobalSettings globalSettings = new GlobalSettings();
    globalSettings.setId(1);
    globalSettings.setCode(GlobalSetting.MULTIUSER_MODE);
    globalSettings.setName("Многопользовательский режим");
    globalSettings.setValue(true);
    globalSettingsList.add(globalSettings);

    GlobalSettings globalSettings2 = new GlobalSettings();
    globalSettings2.setId(2);
    globalSettings2.setCode(GlobalSetting.POST_PREMODERATION);
    globalSettings2.setName("Премодерация постов");
    globalSettings2.setValue(true);
    globalSettingsList.add(globalSettings2);

    GlobalSettings globalSettings3 = new GlobalSettings();
    globalSettings3.setId(3);
    globalSettings3.setCode(GlobalSetting.STATISTICS_IS_PUBLIC);
    globalSettings3.setName("Показывать всем статистику блога");
    globalSettings3.setValue(true);
    globalSettingsList.add(globalSettings3);

    globalSettingsRepository.saveAll(globalSettingsList);
  }
}
