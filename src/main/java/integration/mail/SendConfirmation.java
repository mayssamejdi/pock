package integration.mail;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.delegate.ActivityBehavior;

public class SendConfirmation implements ActivityBehavior {


    @Override
    public void execute(DelegateExecution delegateExecution) {
        System.out.println("Application control mail is delivered succesfuly");
    }
}
