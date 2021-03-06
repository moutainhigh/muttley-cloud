package br.com.muttley.domain;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.security.User;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 23/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Service<T extends Document> {

    boolean isCheckRole();

    String[] getBasicRoles();

    /**
     * Este método é sempre chamado antes de persistir algum registro no banco de dados,
     * e também antes de se chamar o metodo {@link #beforeSave(User, Document)}.
     * Caso queira realizar algum tipo de validação antes de salvar algo, sobrescreva esse método
     * com sua regra de negócio jutamente com suas exceptions.
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser salvo
     */
    void checkPrecondictionSave(final User user, final T value);

    /**
     * Este método é chamado toda vez antes de se salvar algum registro e depois de se chamar o metodo
     * {@link #checkPrecondictionSave(User, Document)}.
     * Este método não deve ser utilizado para executar válidações mas sim para log's, pequenos ajuste
     * e ou regras de négocio antes de se salvar um registro
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser salvo
     */
    void beforeSave(final User user, final T value);

    /**
     * Salva um novo registro no banco de dados,
     * garantindo sempre que ele esteja relacionado a um usuário/owner.
     * <p>
     * Antes de ser salvo qualquer registro, primeiramente é executado a regra
     * de negócio presente no metodo <b>{@link #checkPrecondictionSave(User, Document)}<b/>
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser salvo
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('create', this.getBasicRoles())" +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('create', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    T save(final User user, final T value);

    /**
     * Este metodo é chamado toda vez em que é salvo um registro no banco de dados
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser salvo
     */
    void afterSave(final User user, final T value);

    /**
     * Este método é sempre chamado antes de persistir a atualização de algum registro no banco de dados.
     * Caso queira realizar algum tipo de validação antes de atualizar algo, sobrescreva esse método
     * com sua regra de negócio jutamente com suas exceptions.
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser atualizado
     */
    void checkPrecondictionUpdate(final User user, final T value);

    /**
     * Este método é chamado toda vez antes de se atualizar algum registro e depois de se chamar o metodo
     * {@link #checkPrecondictionUpdate(User, Document)}.
     * Este método não deve ser utilizado para executar válidações mas sim para log's, pequenos ajuste
     * e ou regras de négocio antes de se salvar a alteração em algum registro
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser salvo
     */
    void beforeUpdate(final User user, final T value);

    /**
     * Atualiza um novo registro no banco de dados,
     * garantindo sempre que ele esteja relacionado a um usuário/owner.
     * <p>
     * Antes de ser atualizado qualquer registro, primeiramente é executado a regra
     * de negócio presente no metodo <b>checkPrecondictionUpdate<b/>
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser atualizado
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('update', this.getBasicRoles())" +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('update', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    T update(final User user, final T value);

    /**
     * Este metodo é chamado toda vez em que é atualizado um registro no banco de dados
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser salvo
     */

    void afterUpdate(final User user, final T value);

    /**
     * Busca um registro pelo id
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id procurado
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles()), " +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('simple_use', this.getBasicRoles()) " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()), " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('simple_use', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    T findById(final User user, final String id);

    /**
     * Busca varios registros pelo id
     *
     * @param user -> usuário da requisição corrente
     * @param ids  -> array de id a ser procurado
     */
    @PreAuthorize(
            "this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString() " +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles()), " +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('simple_use', this.getBasicRoles()) " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true"
    )
    Set<T> findByIds(User user, String[] ids);

    /**
     * Pega o primeiro registro que encontrar
     *
     * @param user -> usuário da requisição corrente
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles()), " +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('simple_use', this.getBasicRoles()) " +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()), " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('simple_use', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    T findFirst(final User user);

    /**
     * Busca o histórico de um determinado registro
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id do registro a ser buscado
     * @return Historic
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles())" +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    Historic loadHistoric(final User user, final String id);

    /**
     * Busca o histórico de um determinado registro
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> instancia do registro a ser buscado
     * @return Historic
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles())" +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    Historic loadHistoric(final User user, final T value);

    /**
     * Qualquer regra de négocio que valide o processo de delete deve ser implementada
     * nesse método através de sobrescrita
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id do registro a ser deletado
     */
    void checkPrecondictionDelete(final User user, final String id);

    /**
     * Este método é chamado toda vez antes de se deltetar algum registro e depois de se chamar o metodo
     * {@link #checkPrecondictionDelete(User, String)}.
     * Este método não deve ser utilizado para executar válidações mas sim para log's, pequenos ajuste
     * e ou regras de négocio antes de se deletar algum registro
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id do registro que será deletado
     */
    void beforeDelete(final User user, final String id);

    /**
     * Deleta um registro pelo id
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id procurado
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('delete', this.getBasicRoles())" +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('delete', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    void deleteById(final User user, final String id);


    /**
     * Este metodo é chamado toda vez em que é deletado um registro no banco de dados
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id do registro que foi deletado
     */

    void afterDelete(final User user, final String id);

    /**
     * Este método é chamado toda vez antes de se deltetar algum registro e depois de se chamar o metodo
     * {@link #checkPrecondictionDelete(User, String)}.
     * Este método não deve ser utilizado para executar válidações mas sim para log's, pequenos ajuste
     * e ou regras de négocio antes de se deletar algum registro
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser salvo
     */
    void beforeDelete(final User user, final T value);

    /**
     * Deleta um registro qualquer. Antes de se deletar qualquer registro, o método
     * <b>checkPrecondictionDelete<b/> é chamado para executar devidas validações
     * para se deletar o registro.
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro a ser deletado
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('delete', this.getBasicRoles())" +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('delete', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    void delete(final User user, final T value);

    /**
     * Este metodo é chamado toda vez em que é deletado um registro no banco de dados
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> registro que foi deletado
     */

    void afterDelete(final User user, final T value);

    /**
     * Realiza o processo de count com base nos critérios
     * recebidos como parâmetros;
     *
     * @param user             -> usuário da requisição corrente
     * @param allRequestParams -> Todos os parametros passado na query da requisição
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles())," +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('simple_use', this.getBasicRoles()) " +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()), " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('simple_use', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    Long count(final User user, final Map<String, String> allRequestParams);

    /**
     * Verifica se existe um determinado registro no banco de dados
     *
     * @param user  -> usuário da requisição corrente
     * @param value -> objeto desejado
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles())," +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('simple_use', this.getBasicRoles()) " +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()), " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('simple_use', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    boolean exists(final User user, final T value);

    /**
     * Verifica se existe um determinado registro no banco de dados
     *
     * @param user -> usuário da requisição corrente
     * @param id   -> id do objeto desejado
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles())," +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('simple_use', this.getBasicRoles()) " +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()), " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('simple_use', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    boolean exists(final User user, final String id);

    /**
     * Realiza o processo de listagem com base nos critérios
     * recebidos como parâmetros;
     *
     * @param user             -> usuário da requisição corrente
     * @param allRequestParams -> Todos os parametros passado na query da requisição
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles()), " +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('simple_use', this.getBasicRoles()) " +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    List<T> findAll(final User user, final Map<String, String> allRequestParams);

    /**
     * Verifica se existe algum registro no DB
     */
    @PreAuthorize(
            "        this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles()), " +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('simple_use', this.getBasicRoles()) " +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    boolean isEmpty(final User user);

    /**
     * Recupera valor de uma propriedade especifica de um registro no banco de dados, levando em consideração o id
     */
    @PreAuthorize(
            "this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles()), " +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('simple_use', this.getBasicRoles()) " +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true"
    )
    Object getPropertyValueFromId(final User user, final String id, final String property);

    /**
     * Recupera valor de propriedades especificas de registro no banco de dados, levando em consideração a condição de filtro
     */
    @PreAuthorize(
            "this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles()), " +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('simple_use', this.getBasicRoles()) " +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true"
    )
    Object getPropertyValueFrom(final User user, final Map<String, Object> condictions, final String property);

    /**
     * Recupera valor de propriedades especificas de um registro no banco de dados, levando em consideração a condição de filtro
     */
    @PreAuthorize(
            "this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles()), " +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('simple_use', this.getBasicRoles()) " +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true"
    )
    Object[] getPropertiesValueFrom(final User user, final Map<String, Object> condictions, final String... properties);
}
