package ru.ustits.colleague.commands;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.ustits.colleague.tasks.RepeatTask;

import java.util.Optional;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author ustits
 */
@Log4j2
public final class RepeatCommand extends BotCommand {

  private Scheduler scheduler;

  public RepeatCommand(final String commandIdentifier) {
    super(commandIdentifier, "command for adding repeatable messages");
  }

  @Override
  public void execute(final AbsSender absSender, final User user, final Chat chat, final String[] arguments) {
    if (scheduler == null) {
      try {
        scheduler = StdSchedulerFactory.getDefaultScheduler();
      } catch (SchedulerException e) {
        log.error("Unable to build scheduler", e);
      }
    }

    final Optional<String> text = parseMessage(arguments);
    final Optional<CronExpression> cron = parseCron(arguments);

    try {
      if (scheduleTask(text.get(), cron.get(), absSender)) {
        log.info("{} was scheduled in {}", text, cron);
        absSender.execute(new SendMessage(chat.getId(), "Job scheduled"));
      } else {
        absSender.execute(new SendMessage(chat.getId(), "Failed to schedule job"));
      }
    } catch (TelegramApiException e) {
      log.error("Unable to inform about job", e);
    }
  }

  private boolean scheduleTask(final String text, final CronExpression cron,
                       @NonNull final AbsSender sender) {
    final JobDetail job = buildJob(text, sender);
    final Trigger trigger = buildTrigger(cron, job);
    return scheduleTask(job, trigger);
  }

  private boolean scheduleTask(final JobDetail job, final Trigger trigger) {
    try {
      scheduler.scheduleJob(job, trigger);
    } catch (SchedulerException e) {
      log.error("Unable to start job", e);
      return false;
    }
    return true;
  }

  JobDetail buildJob(final String text, @NonNull final AbsSender sender) {
    final JobDataMap data = new JobDataMap();
    data.put("sender", sender);
    data.put("text", text);
    return newJob(RepeatTask.class)
            .usingJobData(data)
            .build();
  }

  private Trigger buildTrigger(final CronExpression cronExpression, final JobDetail job) {
    return newTrigger()
            .forJob(job)
            .startNow()
            .withSchedule(cronSchedule(cronExpression))
            .build();
  }

  private Optional<String> parseMessage(final String[] arguments) {
    throw new UnsupportedOperationException();
  }

  private Optional<CronExpression> parseCron(final String[] arguments) {
    throw new UnsupportedOperationException();
  }
}
