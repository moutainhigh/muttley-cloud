package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.exception.throwables.security.MuttleySecurityConflictException;
import br.com.muttley.exception.throwables.security.MuttleySecurityNotFoundException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.Passwd;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.repository.UserPreferencesRepository;
import br.com.muttley.security.server.repository.UserRepository;
import br.com.muttley.security.server.service.InmutablesPreferencesService;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserPreferencesRepository preferencesRepository;
    private final JwtTokenUtilService tokenUtil;
    private final String tokenHeader;
    //private final UserPreferencesRepository userPreferencesRepository;
    private final WorkTeamService workTeamService;
    private final InmutablesPreferencesService inmutablesPreferencesService;

    @Autowired
    public UserServiceImpl(final UserRepository repository,
                           final UserPreferencesRepository preferencesRepository,
                           @Value("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader,
                           final JwtTokenUtilService tokenUtil,
                           final WorkTeamService workTeamService,
                           final ObjectProvider<InmutablesPreferencesService> inmutablesPreferencesService) {
        this.repository = repository;
        this.preferencesRepository = preferencesRepository;
        this.tokenHeader = tokenHeader;
        this.tokenUtil = tokenUtil;
        this.workTeamService = workTeamService;
        this.inmutablesPreferencesService = inmutablesPreferencesService.getIfAvailable();
    }

    @Override
    public User save(final User user) {
        final User salvedUser = merge(user);
        salvedUser.setPreferences(this.preferencesRepository.save(new UserPreferences().setUser(salvedUser)));
        return salvedUser;
    }

    @Override
    public void save(final User user, final UserPreferences preferences) {
        preferences.setUser(user);
        this.validatePreferences(preferences);
        this.preferencesRepository.save(preferences);
    }

    @Override
    public boolean remove(final User user) {
        final User other = findById(user.getId());
        repository.delete(other);
        return true;
    }

    @Override
    public boolean removeByUserName(final String userName) {
        return this.remove(findByUserName(userName));
    }

    @Override
    public User update(final User user) {
        return merge(user);
    }

    @Override
    public User updatePasswd(final Passwd passwd) {
        final User user = getUserFromToken(passwd.getToken());
        user.setPasswd(passwd);
        return save(user);
    }

    @Override
    public User findByUserName(final String userName) {
        final User user = repository.findByUserName(userName);
        if (user == null) {
            throw new MuttleySecurityNotFoundException(User.class, "userName", userName + " este registro não foi encontrado");
        }
        return user;
    }

    @Override
    public User findById(final String id) {
        final User user = repository.findOne(id);
        if (user == null) {
            throw new MuttleySecurityNotFoundException(User.class, "id", id + " este registro não foi encontrado");
        }
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return (Collection<User>) repository.findAll();
    }

    @Override
    public User getUserFromToken(final JwtToken token) {
        if (token != null && !token.isEmpty()) {
            final String userName = this.tokenUtil.getUsernameFromToken(token.getToken());
            if (!isNullOrEmpty(userName)) {
                final User user = findByUserName(userName);
                final UserPreferences preferences = this.preferencesRepository.findByUser(user);
                user.setPreferences(preferences);
                user.setCurrentWorkTeam(this.workTeamService.findById(user, preferences.get(UserPreferences.WORK_TEAM_PREFERENCE).getValue().toString()));
                return user;
            }
        }
        throw new MuttleySecurityUnauthorizedException();
    }

   /* @Override
    public Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public JwtUser getCurrentJwtUser() {
        return (JwtUser) getCurrentAuthentication().getPrincipal();
    }

    @Override
    public JwtToken getCurrentToken() {
        return new JwtToken(
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest()
                        .getHeader(this.tokenHeader)
        );
    }

    @Override
    public User getCurrentUser() {
        return getCurrentJwtUser().getOriginUser();
    }*/

    @Override
    public UserPreferences loadPreference(final User user) {
        return this.preferencesRepository.findByUser(user);
    }

    private User merge(final User user) {
        if (user.getName() == null || user.getName().length() < 4) {
            throw new MuttleySecurityBadRequestException(User.class, "nome", "O campo nome deve ter de 4 a 200 caracteres!");
        }
        if (!user.isValidUserName()) {
            throw new MuttleySecurityBadRequestException(User.class, "userName", "Informe um userName válido!");
        }
        if (user.getId() == null) {
            //validando se já existe esse usuário no sistema
            try {
                if (findByUserName(user.getUserName()) != null) {
                    throw new MuttleySecurityConflictException(User.class, "userName", "UserName já cadastrado!");
                }
            } catch (MuttleySecurityNotFoundException ex) {
            }
            //validando se preencheu a senha corretamente
            if (!user.isValidPasswd()) {
                throw new MuttleySecurityBadRequestException(User.class, "passwd", "Informe uma senha válida!");
            }
            return repository.save(user);
        } else {
            final User self = findById(user.getId());

            if (!self.getUserName().equals(user.getUserName())) {
                throw new MuttleySecurityBadRequestException(User.class, "userName", "O userName não pode ser modificado!").setStatus(HttpStatus.NOT_ACCEPTABLE);
            }

            //garantindo que a senha não irá ser modificada
            user.setPasswd(self);

            return repository.save(user);
        }
    }


    public Long count(final User user, final Map<String, Object> allRequestParams) {
        //return repository.count(allRequestParams);
        throw new UnsupportedOperationException("Implemente o methodo");
    }


    public List<User> findAll(final User user, final Map<String, Object> allRequestParams) {
        throw new UnsupportedOperationException("Implemente o methodo");
        //return repository.findAll(allRequestParams);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = repository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        } else {
            return new JwtUser(user);
        }
    }

    private void validatePreferences(final UserPreferences preferences) {
        //se o serviço foi injetado, devemos validar
        //se a preferencia do usuário já tiver o id, devemo validar
        if (this.inmutablesPreferencesService != null && !StringUtils.isEmpty(preferences.getId())) {
            final Set<String> inmutableKeys = this.inmutablesPreferencesService.getInmutablesKeysPreferences();
            if (!CollectionUtils.isEmpty(inmutableKeys)) {
                //recuperando as preferencias sem alterações
                final UserPreferences otherPreferences = this.preferencesRepository.findByUser(preferences.getUser());
                if (otherPreferences != null) {
                    //percorrendo todas a keys que nsão proibidas as alterações
                    inmutableKeys.forEach(inmutableKey -> {
                        if (!StringUtils.isEmpty(inmutableKey)) {
                            //se as preferencias existir no banco
                            if (otherPreferences.contains(inmutableKey)) {
                                //recuperando a preferencia do objeto atual
                                final Preference pre = preferences.get(inmutableKey);
                                //se a preferencial atual for null ou tiver sido modificada
                                if (pre == null || !pre.getValue().equals(otherPreferences.get(inmutableKey).getValue())) {
                                    throw new MuttleyBadRequestException(Preference.class, "key", "Não é possível fazer a alteração da preferencia [" + inmutableKey + ']')
                                            .addDetails("key", inmutableKey)
                                            .addDetails("currentValue", otherPreferences.get(inmutableKey).getValue());
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
