package com.github.youssefwadie.todo.dao.role;

import com.github.youssefwadie.todo.model.Role;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface RoleDao {
    /**
     * Saves a given role. Use the returned instance for further operations as the save operation might have changed the
     * role instance completely.
     *
     * @param role must not be {@literal null}.
     * @return the saved role; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal role} is {@literal null}.
     */
    @Transactional
    @Modifying
    Role save(Role role);


    /**
     * Saves all given roles.
     *
     * @param roles must not be {@literal null} nor must it contain {@literal null}.
     * @return the saved roles; will never be {@literal null}. The returned {@literal Iterable} will have the same size
     * as the {@literal Iterable} passed as an argument.
     * @throws IllegalArgumentException in case the given {@link Iterable roles} or one of its roles is
     *                                  {@literal null}.
     */
    @Transactional
    @Modifying
    Iterable<Role> saveAll(Iterable<Role> roles);


    /**
     * Retrieves a {@link Role role} by its id.
     *
     * @param id must not be {@literal null}.
     * @return the role with the given id or {@literal  Optional#empty()} if non found.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}.
     */
    @Transactional(readOnly = true)
    Optional<Role> findById(Long id);


    /**
     * Returns all roles.
     *
     * @return all roles
     */
    @Transactional(readOnly = true)
    Iterable<Role> findAll();

    /**
     * Returns all {@link Role roles} with the given IDs.
     * <p>
     * If some or all ids are not found, no roles are returned for these IDs.
     * <p>
     *
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return guaranteed to be not {@literal null}. The size can be equal or less than the number of given
     * {@literal ids}.
     * @throws IllegalArgumentException in case the given {@link Iterable ids} or one of its items is {@literal null}.
     */

    @Transactional(readOnly = true)
    Iterable<Role> findAllById(Iterable<Long> ids);

    /**
     * Returns the number of roles available.
     *
     * @return the number of roles.
     */
    @Transactional(readOnly = true)
    long count();

    /**
     * Deletes the role with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    @Transactional
    @Modifying
    void deleteById(Long id);

    /**
     * Deletes the given role.
     *
     * @param role must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal role} is {@literal null} or {@literal role.id} {@literal null}
     */
    @Transactional
    @Modifying
    void delete(Role role);

    /**
     * Deletes all {@link Role roles} with the given IDs.
     *
     * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal ids} or one of its roles is {@literal null}.
     */

    @Transactional
    @Modifying
    void deleteAllById(Iterable<? extends Long> ids);


    /**
     * Deletes the given roles.
     *
     * @param roles must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal roles} or one of the roles is {@literal null}.
     */
    @Transactional
    @Modifying
    void deleteAll(Iterable<? extends Role> roles);

    /**
     * Deletes all roles.
     */
    @Transactional
    @Modifying
    void deleteAll();
}
