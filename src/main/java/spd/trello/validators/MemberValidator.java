package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Member;
import spd.trello.exception.ResourceNotFoundException;
import spd.trello.repository.MemberRepository;
import spd.trello.repository.UserRepository;

@Component
public class MemberValidator extends AbstractValidator<Member> {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final HelperValidator<Member> helper;

    public MemberValidator
            (UserRepository userRepository, MemberRepository memberRepository, HelperValidator<Member> helper) {
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.helper = helper;
    }

    private void validateMemberFields(Member entity) {
        if (entity.getUserId() == null) {
            throw new ResourceNotFoundException("The userId field must be filled.");
        }
    }

    @Override
    public void validateSaveEntity(Member entity) {
        validateMemberFields(entity);
        StringBuilder exceptions = helper.validateCreateEntity(entity);
        if (!userRepository.existsById(entity.getUserId())) {
            exceptions.append("The userId field must belong to a user. \n");
        }
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Member entity) {
        validateMemberFields(entity);
        var oldMember = memberRepository.findById(entity.getId());
        if (oldMember.isEmpty()) {
            throw new ResourceNotFoundException("Can not update non-existent member!");
        }
        StringBuilder exceptions = helper.validateEntityUpdate(oldMember.get(), entity);
        if (!oldMember.get().getUserId().equals(entity.getUserId())) {
            exceptions.append("Member can not be transferred to another user. \n");
        }
        helper.throwException(exceptions);
    }


}
