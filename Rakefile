
namespace :doc do
  directory 'doc' => 'project.clj' do
    sh "git clone git@github.com:cddr/schema.contrib.git doc"
    sh "cd doc && git symbolic-ref HEAD refs/heads/gh-pages"
    sh "cd doc && rm .git/index"
    sh "cd doc && git clean -fdx"
    sh "cd doc && git checkout gh-pages"
    sh "lein doc"
  end

  task :generate => 'doc'
  task :publish => 'doc' do
    if sh "git diff --exit-code"
      puts "API Documentation is unchanged"
    else
      sh "cd doc && git add ."
      sh "cd doc && git commit -am 'Update API Documentation'"
      sh "cd doc && git push -u origin gh-pages"
    end
  end
end

