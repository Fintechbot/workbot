package ru.ustits.colleague.commands.repeats;

import ru.ustits.colleague.tools.CronBuilder;
import ru.ustits.colleague.tools.StringUtils;

import static ru.ustits.colleague.tools.StringUtils.split;

/**
 * @author ustits
 */
public final class DailyStrategy implements RepeatStrategy {

  private static final Integer PARAMETERS_COUNT = 3;

  @Override
  public String transformCron(final String cron) {
    final String[] args = split(cron);
    final String hours = args[0];
    final String minutes = args[1];
    return CronBuilder.builder()
            .withSeconds(StringUtils.ZERO)
            .withHours(hours)
            .withMinutes(minutes)
            .build();
  }

  @Override
  public int parametersCount() {
    return PARAMETERS_COUNT;
  }
}
