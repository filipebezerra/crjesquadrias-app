package br.com.libertsolutions.crs.app.domain.pojo;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class LoginBody {
    String cpf;
    String senha;

    public static LoginBody newLoginBody(String cpf, String senha) {
        return new LoginBody()
                .setCpf(cpf)
                .setSenha(senha);
    }

    public String getCpf() {
        return cpf;
    }

    public LoginBody setCpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public String getSenha() {
        return senha;
    }

    public LoginBody setSenha(String senha) {
        this.senha = senha;
        return this;
    }
}
